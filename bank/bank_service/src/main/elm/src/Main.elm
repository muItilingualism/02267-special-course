port module Main exposing (main)

import Browser
import Browser.Navigation as Nav
import Html exposing (..)
import Html.Events exposing (..)
import Html.Attributes exposing (..)
import Url
import Json.Encode as Encode
import Http
import Html.Entity
import Html.Keyed
import Http exposing (..)
import Json.Decode exposing (Decoder, list, field, string, decodeString, float, map5, nullable, andThen, maybe)
import Time
import List exposing (take)
import Element exposing (Element)
import Element.Background as Background
import Element.Border as Border
import Element.Font as Font
import Element.Input as Input
import String exposing (fromFloat)
import Round

gocartUrlWithId : Maybe String -> String -> String
gocartUrlWithId id rest =
  case id of 
    Nothing -> "/rest/gocarts/" ++ "--missingid--" ++ rest
    Just ident -> "/rest/gocarts/" ++ ident ++ rest

-- JavaScript usage: app.ports.onMessage.send(response);
port onMessage : (String -> msg) -> Sub msg
-- port onOpen : (String -> msg) -> Sub msg
-- port onClose : (String -> msg) -> Sub msg
-- port onError : (String -> msg) -> Sub msg
-- JavaScript usage: app.ports.send.subscribe(handler);
port send : String -> Cmd msg

-- MAIN

type Screens = StartupScreen | CreateRaceScreen |  JoinRaceScreen | RaceScreen | ResultScreen  

main : Program () Model Msg
main =
  Browser.element
    { init = init
    , view = view
    , update = update
    , subscriptions = subscriptions
    }


-- MODEL

type alias Model =
  { screen : Screens
    , race : RaceModel
    , gocartoptions : List String
    , raceoptions    : List String
    , driveroptions : List String
    , playeroptions : List String
    , trackoptions : List String
    , selectionrace : Maybe String
    , selectiongocart : Maybe String
    , selectiondriver : Maybe String
    , selectionplayer : Maybe String
    , selectiontrack : Maybe String
  }

type Status =
    Initial
  | Starting 
  | Started (Result Http.Error String) 
  | Stopping 
  | Stopped (Result Http.Error String)

type alias TpvReport =
  { time: Maybe (Maybe String)
  , longitude : Maybe (Maybe Float)
  , latitude : Maybe (Maybe Float)
  , altitude : Maybe (Maybe Float)
  , speed : Maybe (Maybe Float)
  }

type alias RaceModel =
  { status : Status
  , locations : (Result Http.Error (List TpvReport))
  , tracks : (Result Http.Error (List String)) 
  , delete : Maybe String
  , speed : Maybe Float
  , gocart : Maybe String
  ,race : Maybe String
  }

nothingStringGocart = "-- select gocart --"
nothingStringDriver = "-- select driver --"
nothingStringPlayer = "-- select player --"
nothingStringTrack = "-- select track --"
nothingStringRace = "-- select race --"
init : () -> ( Model, Cmd Msg )
init flags =
  ( initialModel
    , initialCmd )

initialCmd = Cmd.batch [getGocarts, getUsers, getTracks]

initialModel = { screen = StartupScreen, 
      race = initialRaceModel,
      raceoptions = [nothingStringRace],
      gocartoptions = [nothingStringGocart],
      driveroptions = [nothingStringDriver],
      playeroptions = [nothingStringPlayer],
      trackoptions = [nothingStringTrack],
      selectionrace = Nothing,
      selectiongocart = Nothing, 
      selectiondriver = Nothing,
      selectionplayer = Nothing,
      selectiontrack = Nothing}

initialRaceModel =
  { status = Initial, locations = Ok [], tracks = Ok [], delete = Nothing, speed=Nothing, gocart = Nothing, race = Nothing}

-- UPDATE

type Msg
  = NextScreen
  | RaceList (Result Http.Error (List String))
  | GocartList (Result Http.Error (List String))
  | UsersList (Result Http.Error (List String))
  | TracksList (Result Http.Error (List String))
  | SelectedRace String
  | SelectedGocart String
  | SelectedDriver String
  | SelectedPlayer String
  | SelectedTrack String
  | RaceCreated (Result Http.Error String)
  | TeamCreated (Result Http.Error String)
  | SetTrackforRace(Result Http.Error String)
  | RaceMsg RaceMsg
  | Cancel
  | JoinRace
  | CreateRace

type RaceMsg 
  = Start
  | Stop
  | WaitStarted (Result Http.Error String)
  | WaitStopped (Result Http.Error String)
  | Noop 
  | Tick Time.Posix
  | NewLocations (Result Error (List TpvReport))
  | NewTracks (Result Error (List String))
  | Delete String
  | TrackDeleted (Result Error String)
  | DeleteConfirmed 
  | DeleteCancelled 
  | GpsStatus (Result Error String)
  | OnMessage String
  | Faster
  | Slower
  | CommandSent (Result Error String)

updatedriveroptions : Model -> Maybe String -> Model
updatedriveroptions model user =
  case user of 
    Nothing -> model 
    Just u -> { model | driveroptions = List.filter (\u1 -> u1 /= u) model.driveroptions }

updateplayeroptions : Model -> Maybe String -> Model
updateplayeroptions model user =
  case user of 
    Nothing -> model 
    Just u -> { model | playeroptions = List.filter (\u1 -> u1 /= u) model.playeroptions }

update : Msg -> Model -> ( Model, Cmd Msg )
update msg model =
  case msg of
    NextScreen ->
      case model.screen of 
        StartupScreen -> (model , Cmd.none) -- NextScreen msg is not generated on the StartupScreen
        CreateRaceScreen -> 
            ({ model |screen = StartupScreen }
              , Cmd.batch [createRace model] )
        JoinRaceScreen -> 
          let 
            raceModel = { initialRaceModel | gocart = model.selectiongocart}
          in 
          ({ model| screen = RaceScreen, race = raceModel}, Cmd.batch[createTeam model, getTrackForRace model])
        RaceScreen -> ({ model | screen = ResultScreen}, Cmd.none)
        ResultScreen -> (initialModel, initialCmd)
    CreateRace ->
      case model.screen of 
        CreateRaceScreen -> (model , Cmd.none)
        RaceScreen -> (model , Cmd.none)
        ResultScreen -> (model , Cmd.none)
        StartupScreen -> ({model| screen = CreateRaceScreen} , Cmd.none)
        JoinRaceScreen -> (model , Cmd.none)
    JoinRace ->
      case model.screen of 
        CreateRaceScreen -> (model , Cmd.none)
        RaceScreen -> (model , Cmd.none)
        ResultScreen -> (model , Cmd.none)
        StartupScreen -> ({model| screen = JoinRaceScreen} , getRaces )
        JoinRaceScreen -> (model , Cmd.none)
    SelectedRace s -> 
      let 
        selection = selectionFromString s
        raceModel = model.race
      in
        let 
          newRaceModel = { raceModel | race = selection }
        in  
          ({ model | selectionrace = selection, race = newRaceModel }, Cmd.none)
    SelectedGocart s -> 
      let 
        selection = selectionFromString s
        raceModel = model.race
      in
        let 
          newRaceModel = { raceModel | gocart = selection }
        in  
          ({ model | selectiongocart = selection, race = newRaceModel }, Cmd.none)
    SelectedDriver s -> 
      let 
        selection = selectionFromString s
      in 
        (updateplayeroptions { model | selectiondriver = selection } selection, Cmd.none)
    SelectedPlayer s -> 
      let 
        selection = selectionFromString s
      in 
        (updatedriveroptions { model | selectionplayer = (selectionFromString s)} selection, Cmd.none)
    SelectedTrack s -> ({ model | selectiontrack = (selectionFromString s)}, Cmd.none)
    
    SetTrackforRace r ->
      case r of 
        Ok s ->  ({model | selectiontrack = (selectionFromString s)}, Cmd.none)
        Err m -> ({model | selectiontrack = Nothing}, Cmd.none)   

    RaceList r ->
      case r of 
        Ok s ->  ({model| raceoptions = model.raceoptions ++ s}, Cmd.none)
        Err m -> ({model | raceoptions = ["no options found"]}, Cmd.none)    
    GocartList r ->
      case r of 
        Ok s ->  ({model| gocartoptions = model.gocartoptions ++ s}, Cmd.none)
        Err m -> ({model | gocartoptions = ["no options found"]}, Cmd.none)
    UsersList r ->
      case r of 
        Ok s ->  ({model| driveroptions = [nothingStringDriver] ++ s, playeroptions = [nothingStringPlayer] ++ s}, Cmd.none)
        Err m -> ({model | driveroptions = ["no options found"], playeroptions = ["no options found"]}, Cmd.none)
    TracksList r ->
      case r of 
        Ok s ->  ({model| trackoptions = model.trackoptions ++ s}, Cmd.none)
        Err m -> ({model | trackoptions = ["no options found"]}, Cmd.none)
    RaceCreated _ -> (model, Cmd.none)
    TeamCreated _ -> (model, sendGocartThroughWebsocket model.selectiongocart)
    RaceMsg rmsg -> 
      let 
        (newRaceModel, commands) = updateRaceModel rmsg model.race
      in 
        ({model | race=newRaceModel}, Cmd.map RaceMsg commands)
    Cancel -> ({model | selectiongocart = Nothing
                      , selectiondriver = Nothing
                      , selectionplayer = Nothing
                      , selectiontrack = Nothing
                      , selectionrace = Nothing}
              , getUsers)

selectionFromString : String -> Maybe String
selectionFromString s =
  if    s == nothingStringGocart 
     || s == nothingStringDriver 
     || s == nothingStringPlayer 
     || s == nothingStringTrack 
  then
    Nothing
  else
    Just s

getRaces : (Cmd Msg)
getRaces =
    Http.get
          {url = "/rest/races"
          , expect = Http.expectJson RaceList (Json.Decode.list string)
          }
getGocarts : (Cmd Msg)
getGocarts =
    Http.get
          {url = "/rest/gocarts"
          , expect = Http.expectJson GocartList (Json.Decode.list string)
          }

getUsers : (Cmd Msg)
getUsers =
    Http.get
          {url = "/rest/users"
          , expect = Http.expectJson UsersList (Json.Decode.list string)
          }

getTracks : (Cmd Msg)
getTracks =
    Http.get
          {url = "/rest/tracks"
          , expect = Http.expectJson TracksList (Json.Decode.list string)
          }
getTrackForRace : Model -> (Cmd Msg)
getTrackForRace  model =
    Http.get
          {url = "/rest/races/" ++ (toString model.selectionrace) ++ "/track"
          , expect = Http.expectString SetTrackforRace
          }


createRace : Model -> (Cmd Msg)
createRace model =
    Http.request
       {
        method = "POST"
        , url = "/rest/races"
        , body = Http.jsonBody (Encode.object  
        [ 
          ("trackName", Encode.string (toString model.selectiontrack)) 
        ])
        , expect = Http.expectString RaceCreated
        , headers = [Http.header "Accept" "application/json"]
        , timeout = Nothing
        , tracker = Nothing
        }

createTeam : Model -> (Cmd Msg)
createTeam model =
    Http.request
       {
        method = "POST"
        , url = "/rest/races/" ++ (toString model.selectionrace) ++ "/teams"
        , body = Http.jsonBody (Encode.object  
        [ 
          ("driverId", Encode.string (toString model.selectiondriver)), 
          ("playerId", Encode.string (toString model.selectionplayer)), 
          ("gocartName", Encode.string (toString model.selectiongocart))
        ])
        , expect = Http.expectString TeamCreated
        , headers = [Http.header "Accept" "application/json"]
        , timeout = Nothing
        , tracker = Nothing
        }        
sendGocartThroughWebsocket : Maybe String -> Cmd msg 
sendGocartThroughWebsocket gocart = 
  case gocart of 
    Nothing -> Cmd.none 
    Just gid -> send gid

updateRaceModel : RaceMsg -> RaceModel -> (RaceModel,Cmd RaceMsg)
updateRaceModel msg model =
  let getMsgForStatus status =
        if status == "started"
        then WaitStarted
        else WaitStopped

      putGpsStatus status =
        Http.request
          {
            method = "PUT"
          , url = gocartUrlWithId model.gocart "/status"
          , body = Http.jsonBody (Encode.object  [("status", Encode.string status)])
          , expect = Http.expectJson (getMsgForStatus status) (field "status" string)
          , headers = [Http.header "Accept" "application/json"]
          , timeout = Nothing
          , tracker = Nothing
          }
  in
  case msg of
    Start ->
      (clearDelete { model | status = Starting}
      ,putGpsStatus "started"
      )
    Stop ->
      (clearDelete { model | status = Stopping}
      ,putGpsStatus "stopped"
      )
    WaitStarted r ->
      (clearDelete {model | status = Started r}, Cmd.none) -- There is an issue here that the file that is currently written will be listed!!
    WaitStopped r ->
      (clearDelete {model | status = Stopped r}, getTracks2 model)
    Noop -> (clearDelete model, Cmd.none)
    Tick _ ->
      tickUpdate model
    NewLocations r ->
      case r of 
        Ok ls -> ({model | locations = Ok (List.take 1 ls)}, Cmd.none)
        Err _ -> (model, Cmd.none)
    NewTracks r ->
      (clearDelete {model | tracks = r}, Cmd.none)
    Delete id -> ({model | delete = Just id}, Cmd.none)
    TrackDeleted _ -> (clearDelete model, getTracks2 model)
    DeleteCancelled -> (clearDelete model, Cmd.none)
    DeleteConfirmed -> (clearDelete model, deleteTrack model (model.delete))
    GpsStatus r ->
      case r of 
        Ok s -> if s == "started" then ({model| status = Started r}, Cmd.none)
             else ({model | status= Stopped r}, Cmd.none)
        Err m -> ({model | status = Stopped r}, Cmd.none)
    OnMessage m -> 
      let 
          result = tpvFromJsonString (decodeString (field "data" string) m) 
          ls = case model.locations of 
                Ok locs -> locs 
                Err _ -> []
          speed str = List.head (List.drop 3 (String.split "," str))
      in 
        case result of 
             Ok tpv -> ({model | 
                          locations = Ok [tpv]
                          , speed = (toMaybeFloat tpv.speed)}
                        ,Cmd.none)
             Err err -> ({model| locations = Ok []},Cmd.none)
    Faster -> (model, putMotorSpeed model "FASTER")
    Slower -> (model, putMotorSpeed model "SLOWER")
    CommandSent _ -> (model, Cmd.none)

putMotorSpeed : RaceModel -> String -> Cmd RaceMsg
putMotorSpeed model speed =
  Http.request
    {
    method = "PUT"
    , url = gocartUrlWithId model.gocart "/motor/speed"
    , body = (Http.stringBody "text/plain" speed)
    , expect = Http.expectString CommandSent
    , headers = [Http.header "Accept" "text/plain"]
    , timeout = Nothing
    , tracker = Nothing
    }


toMaybeFloat : Maybe (Maybe Float) -> Maybe Float
toMaybeFloat mfl =
  case mfl of 
    Just fl -> fl 
    Nothing -> Nothing

errorTpv: Json.Decode.Error -> TpvReport
errorTpv err =
  { time = Just (Just (Json.Decode.errorToString err))
    ,longitude = Nothing
    , latitude = Nothing
    , altitude = Nothing
    , speed = Nothing}

tpvFromJsonString tpv_s =
  case tpv_s of 
    Ok s -> decodeString tpvReportDecoder s 
    Err e -> Err e


clearDelete model =
  {model | delete = Nothing}

tickUpdate : RaceModel -> (RaceModel,Cmd RaceMsg)
tickUpdate model = 
   let
      callHttp = 
        (model
        ,getLocations model
        )
   in
   case model.status of
      Initial -> callHttp
      Starting -> callHttp
      Stopping -> callHttp
      Started r -> 
        case r of
          Ok _ -> callHttp
          Err _ -> (model, Cmd.none)
      Stopped _ -> (model, Cmd.none)

-- COMMANDS

getLocations : RaceModel -> (Cmd RaceMsg)
getLocations model =
  Http.get
          {url = gocartUrlWithId model.gocart "/data?last=10"
          , expect = Http.expectJson NewLocations (Json.Decode.list tpvReportDecoder)
          }

tpvReportDecoder : Decoder TpvReport
tpvReportDecoder =
  map5 TpvReport
    (maybe (field "time" (maybe string)))
    (maybe (field "longitude" (maybe float)))
    (maybe (field "latitude" (maybe float)))
    (maybe (field "altitude" (maybe float)))
    (maybe (field "speed" (maybe float)))

getTracks2 : RaceModel -> (Cmd RaceMsg)
getTracks2 model =
  Http.request
          {
          method = "GET"
        , headers = [Http.header "Accept" "application/json"]
        , url = gocartUrlWithId model.gocart "/data/tracks?last=10"
        , body = Http.emptyBody
        , expect = Http.expectJson NewTracks (Json.Decode.list string)
        , timeout = Nothing
        , tracker = Nothing
          }

deleteTrack : RaceModel -> Maybe String -> (Cmd RaceMsg)
deleteTrack model mid =
  case mid of 
    Nothing -> Cmd.none
    Just id -> 
       Http.request
        { method = "DELETE"
        , headers = []
        , url = gocartUrlWithId model.gocart ("/data/tracks/" ++ id)
        , body = Http.emptyBody
        , expect = Http.expectString TrackDeleted
        , timeout = Nothing
        , tracker = Nothing
        } 


-- SUBSCRIPTIONS

subscriptions : Model -> Sub Msg
subscriptions model =
      Sub.batch[Sub.map RaceMsg (onMessage OnMessage)]

-- VIEW

view : Model -> Html Msg
view model =
  case model.screen of 
    StartupScreen -> viewStartupScreen model
    JoinRaceScreen -> viewJoinRaceScreen model
    CreateRaceScreen -> viewCreateRaceScreen model
    RaceScreen -> viewRaceScreen model
    ResultScreen -> viewResultScreen model 

viewStartupScreen model = 
  div [class "w3-light-grey w3-padding-64 w3-center"]
    [ button [ onClick CreateRace ] [ text "create a race" ]
    , button [ onClick JoinRace ] [ text "join a race" ]
    ]
viewJoinRaceScreen model =
  div [class "w3-light-grey w3-padding-64 w3-center"] [
    h3 [] [text "Join existing Race"]
    , br [] []
    , span [] [text "Races: "]
    , selectHtmlElementRace model
    , br [] []
    , span [] [text "Gocart: "]
    , selectHtmlElementGocart model
    , br [] []
    , span [] [text "Driver: "]
    , selectHtmlElementDriver model
    , br [] []
    , span [] [text "Player: "]
    , selectHtmlElementPlayer model
    , br [] []
    , div [] [  if model.selectionplayer /= Nothing then button [onClick NextScreen, class "w3-button" ] [text "join"] else text Html.Entity.nbsp
              , text Html.Entity.nbsp
              , button [onClick Cancel, class "w3-button"] [text "Reset"]]
  ]
viewCreateRaceScreen model =
  div [class "w3-light-grey w3-padding-64 w3-center"] [
    h3 [] [text "Race Setup"]
    , br [] []
    , span [] [text "Track: "]
    , selectHtmlElementTrack model
    , br [] []
    , div [] [  if model.selectiontrack /= Nothing then button [onClick NextScreen, class "w3-button" ] [text "Ok"] else text Html.Entity.nbsp
              , text Html.Entity.nbsp
              , button [onClick Cancel, class "w3-button"] [text "Reset"]]
  ]
  
selectHtmlElementRace model =
  if model.raceoptions /= [nothingStringRace]
  then 
    case model.selectionrace of 
      Nothing -> select [onInput SelectedRace] (List.map optionElementForRace model.raceoptions)
      Just raceId -> text (viewRaceId raceId)
  else
    text ""
    
viewRaceId raceId = 
  if raceId == "-- select race --"
  then
      raceId
  else 
    "race " ++ raceId

selectHtmlElementGocart model =
  if model.selectionrace /= Nothing && model.gocartoptions /= [nothingStringGocart]
  then 
    case model.selectiongocart of 
      Nothing -> select [onInput SelectedGocart] (List.map optionElement model.gocartoptions)
      Just gocart -> text gocart
  else
    text ""

selectHtmlElementDriver model =
  if model.selectiongocart /= Nothing && model.driveroptions /= [nothingStringDriver]
  then 
    case model.selectiondriver of 
      Nothing -> select [onInput SelectedDriver] (List.map optionElement model.driveroptions)
      Just driver -> text driver
  else
    text ""

selectHtmlElementPlayer model =
  if model.selectiondriver /= Nothing && model.playeroptions /= [nothingStringPlayer]
  then 
    case model.selectionplayer of 
      Nothing -> select [onInput SelectedPlayer] (List.map optionElement model.playeroptions) 
      Just player -> text player
  else
    text ""

selectHtmlElementTrack model =
  if model.trackoptions /= [nothingStringTrack]
  then 
    case model.selectiontrack of 
      Nothing -> select [onInput SelectedTrack] (List.map optionElement model.trackoptions)
      Just track -> text track
  else
    text ""

optionElement v =
    option [value v] [text v]
    
optionElementForRace raceId =
    option [value raceId] [text (viewRaceId raceId)]
    
toString maybe =
  case maybe of 
    Nothing -> "nothing"
    Just s -> s

viewRaceScreen model =
  let
    showButton b =
      case model.race.status of 
        Initial -> div [] [] 
        Starting -> div [] [] 
        Stopping -> div [] [] 
        Stopped _ -> b 
        Started _ -> div [] []
    btn = button [onClick NextScreen, class "w3-button" ] [text "Results"]
  in 
    div [class "w3-light-grey w3-padding-64 w3-center"] [
      viewRaceInfo model 
    , br [] []
    , br [] []
    , div [] [Html.map RaceMsg (viewRace model.race)]
    , br [] []
    , showButton btn
    ]

viewRaceInfo model =
      Html.small [] [
      span [] [text "Driver: "]
      , strong [] [text (Maybe.withDefault "unknown driver" model.selectiondriver)]
      , text ";  "
      , span [] [text "Player: "]
      , strong [] [text (Maybe.withDefault "unknown player" model.selectionplayer)]
      , text ";  "
      , span [] [text "Gocart: "]
      , strong [] [text (Maybe.withDefault "unknown gocart" model.selectiongocart)]
      , text ";  "
      , span [] [text "Track: "]
      , strong [] [text (Maybe.withDefault "unknown track" model.selectiontrack )]
      ]

viewResultScreen model =
  div [class "w3-light-grey w3-padding-64 w3-center"] [
    Html.map RaceMsg (viewResults model.race)
    , br [] []
    , button [ onClick NextScreen, class "w3-button" ] [text "New Game"]
  ]

viewResults : RaceModel -> Html RaceMsg
viewResults model =
    div [] [
      h3 [] [text "Recent Races"]
      ,viewTracks model
    ]

viewRace : RaceModel -> Html RaceMsg
viewRace model =
  let 
    startButton btn = 
      case model.status of
        Initial -> btn 
        Started _ -> btn
        Starting -> btn
        Stopping -> btn
        Stopped _ -> div [] []
    b = button [ onClick (viewMsg model), class ("w3-button w3-block " ++ (viewColor model))] 
               [ text (viewCommandText model) ]
  in
    div [(style "margin" "auto"), (style "width" "60%")] [
      startButton b
      ,br [] []
      ,text (viewSpeed model)
      ,br [] []
      ,text (convertTpvReportToString (List.head (Result.withDefault [] model.locations)))
      ,br [] []
      ,div [] [fasterSlowerButtons model]
    ]

fasterSlowerButtons model = 
  let 
    btn = span [] [  button [ onClick Slower, class "w3-button"  ] [text "slower"]
               , text Html.Entity.nbsp
               ,button [ onClick Faster, class "w3-button" ] [text "faster"]]
  in 
  case model.status of 
        Initial -> text ""
        Started _ -> btn
        Starting -> text ""
        Stopping -> text ""
        Stopped _ -> text ""
      

-- viewLocations : RaceModel -> Html RaceMsg
-- viewLocations model = 
--   case model.locations of 
--     Ok ls -> div [] (List.map (\s -> (text (convertTpvReportToString s))) ls)
--     Err msg -> viewErrorText msg

convertTpvReportToString: Maybe TpvReport -> String
convertTpvReportToString mtpv =
  case mtpv of 
    Nothing -> ""
    Just tpv -> 
      "long = "++ (Maybe.withDefault "null" (Maybe.map (Round.round 7) (Maybe.withDefault Nothing tpv.longitude)))
      ++ ", "
      ++ "lat = "++ (Maybe.withDefault "null" (Maybe.map (Round.round 7) (Maybe.withDefault Nothing tpv.latitude)))


-- convertTpvReportToString: Maybe TpvReport -> String
-- convertTpvReportToString mtpv =
--   case mtpv of 
--     Nothing -> ""
--     Just tpv -> 
--       convertMaybeStringToString tpv.time 
--       ++ ": "
--       ++ "long = "++convertFloatToString tpv.longitude 
--       ++ ", "
--       ++ "lat = "++convertFloatToString tpv.latitude
--       ++", "
--       ++ "alt = "++convertFloatToString tpv.altitude
--       ++", "
--       ++ "speed (m/s) = "++convertFloatToString tpv.speed

convertFloatToString: Maybe (Maybe Float) -> String
convertFloatToString fl =
  case fl of 
    Just f -> 
      case f of 
        Just f1 -> fromFloat f1
        Nothing -> "null"
    Nothing -> "null"

convertMaybeStringToString: Maybe (Maybe String) -> String
convertMaybeStringToString mstr =
  case mstr of 
    Just s ->
      case s of 
        Just s1 -> s1 
        Nothing -> "null"
    Nothing -> "null"

viewTracks : RaceModel -> Html RaceMsg
viewTracks model = 
  case model.tracks of 
    Ok ls -> Html.Keyed.ul [class "w3-ul"] (List.map (\id -> (id,li [class "w3-ul w3-li"] (createLink model id))) ls)
    Err msg -> viewErrorText msg

viewSpeed : RaceModel -> String
viewSpeed model = 
  case model.speed of 
    Nothing -> ""
    Just speed -> ((speedInKmph speed)++" km/h")

speedInKmph speed =
      Round.round 0 (speed / 1000 * 60*60)

viewErrorText msg =
  div [class "w3-red"] [text (getErrorText msg)]

viewMsg : RaceModel -> RaceMsg
viewMsg model =
  case model.status of 
    Initial -> Start
    Started r ->
      case r of
        Ok _ -> Stop
        Err _ -> Start
    Stopped r ->
      case r of
        Ok _ -> Start
        Err _ -> Stop
    Starting -> Noop
    Stopping -> Noop

viewCommandText : RaceModel -> String
viewCommandText model =
  case model.status of 
    Initial -> "Start"
    Started r ->
      case r of
        Ok _ -> "Stop"
        Err msg -> getErrorText msg
    Stopped r ->
      case r of
        Ok _ -> "Start"
        Err msg -> getErrorText msg
    Starting -> "Starting"
    Stopping -> "Stopping"

viewColor : RaceModel -> String
viewColor model =
  " " ++
  (case model.status of 
    Initial -> "w3-green"
    Starting -> "w3-green"
    Stopping -> "w3-blue"
    Started r ->
      case r of
        Ok _ -> "w3-blue"
        Err  _ -> "w3-red"
    Stopped r ->
      case r of
        Ok  _ -> "w3-green"
        Err _ -> "w3-red")
  ++ " "

getErrorText : Http.Error -> String
getErrorText r =
  case r of 
    BadUrl str -> "Bad URL: " ++ str
    Timeout -> "Timeout"
    NetworkError -> "Network Error"
    BadStatus i -> "Status " ++ String.fromInt i
    BadBody str -> "Body " ++ str

createLink : RaceModel -> String -> List (Html RaceMsg)
createLink model id =
  [a [href (gocartUrlWithId model.gocart ("/data/tracks/kml/" ++ id ++ ".kml")), target "_blank"] [text id]
  ,text Html.Entity.nbsp
  ,text "("
  ,a [href (gocartUrlWithId model.gocart ("/data/tracks/csv/" ++ id ++ ".csv")), target "_blank"] [text "csv"]
  ,text ")"
  ,text Html.Entity.nbsp
  ,deleteButtonPossibleConfirmation model id]

deleteButtonPossibleConfirmation model id =
  let 
    deleteButton = button [onClick (Delete id), class "w3-button"] [div [class "w3-text-red"] [b [] [text Html.Entity.cross]]]
  in 
    case model.delete of 
      Nothing -> deleteButton 
      Just did -> 
       if did == id then 
        div [class "w3-red"] [b [] [text "Really delete?"], button [ onClick DeleteConfirmed, class "w3-button"] [text "Yes"], button [onClick DeleteCancelled, class "w3-button"] [text "No"]]
       else
        deleteButton

