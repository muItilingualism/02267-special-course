port module Main exposing (main)

import Browser
import Html exposing (..)
import Html.Events exposing (..)
import Html.Attributes exposing (..)
import Html.Entity
import Html.Keyed
import Http exposing (..)
import Json.Decode exposing (Decoder, field, string, decodeString, float, map5, nullable, andThen, maybe)
import Json.Encode as Encode
import Time
import List exposing (take)
import Element exposing (Element)
import Element.Background as Background
import Element.Border as Border
import Element.Font as Font
import Element.Input as Input
import String exposing (fromFloat)

urlWithBase : String -> String
urlWithBase url = 
 -- "http://bocart.compute.dtu.dk:8080"++url
 url

-- JavaScript usage: app.ports.onMessage.send(response);
port onMessage : (String -> msg) -> Sub msg
-- port onOpen : (String -> msg) -> Sub msg
-- port onClose : (String -> msg) -> Sub msg
-- port onError : (String -> msg) -> Sub msg
-- JavaScript usage: app.ports.send.subscribe(handler);
-- port send : String -> Cmd msg

main =
  Browser.element
    { init = init
    , update = update
    , subscriptions = subscriptions
    , view = view
    }

-- MODEL

type Status =
  Starting 
  | Stopping 
  | Started (Result Http.Error String) 
  | Stopped (Result Http.Error String)

type alias TpvReport =
  { time: Maybe (Maybe String)
  , longitude : Maybe (Maybe Float)
  , latitude : Maybe (Maybe Float)
  , altitude : Maybe (Maybe Float)
  , speed : Maybe (Maybe Float)
  }

type alias Model =
  { status : Status
  , locations : (Result Http.Error (List TpvReport))
  , tracks : (Result Http.Error (List String)) 
  , delete : Maybe String
  , speed : Maybe Float
  }

init : () -> (Model, Cmd Msg) 
init _ = 
  ({ status = Stopped (Ok ""), locations = Ok [], tracks = Ok [], delete = Nothing, speed=Nothing},
    Cmd.batch [getGpsStatus, getLocations,getTracks]
  )

-- UPDATE

type Msg 
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

update : Msg -> Model -> (Model,Cmd Msg)
update msg model =
  let getMsgForStatus status =
        if status == "started"
        then WaitStarted
        else WaitStopped

      putGpsStatus status =
        Http.request
          {
            method = "PUT"
          , url = urlWithBase "/rest/gps/status"
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
      (clearDelete {model | status = Stopped r}, getTracks)
    Noop -> (clearDelete model, Cmd.none)
    Tick _ ->
      tickUpdate model
    NewLocations r ->
        ({model| locations = r}, Cmd.none)
    NewTracks r ->
      (clearDelete {model | tracks = r}, Cmd.none)
    Delete id -> ({model | delete = Just id}, Cmd.none)
    TrackDeleted _ -> (clearDelete model, getTracks)
    DeleteCancelled -> (clearDelete model, Cmd.none)
    DeleteConfirmed -> (clearDelete model, deleteTrack (model.delete))
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
                          locations = Ok (take 10 (tpv::ls))
                          , speed = (toMaybeFloat tpv.speed)}
                        ,Cmd.none)
             Err err -> ({model| locations = Ok (take 10 ((errorTpv err)::ls))},Cmd.none)
    Faster -> (model, putMotorSpeed "FASTER")
    Slower -> (model, putMotorSpeed "SLOWER")
    CommandSent _ -> (model, Cmd.none)

putMotorSpeed : String -> Cmd Msg
putMotorSpeed speed =
  Http.request
    {
    method = "PUT"
    , url = urlWithBase "/rest/motor/speed"
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

tickUpdate : Model -> (Model,Cmd Msg)
tickUpdate model = 
   let
      callHttp = 
        (model
        ,getLocations
        )
   in
   case model.status of
      Starting -> callHttp
      Stopping -> callHttp
      Started r -> 
        case r of
          Ok _ -> callHttp
          Err _ -> (model, Cmd.none)
      Stopped _ -> (model, Cmd.none)

-- COMMANDS

getLocations : (Cmd Msg)
getLocations =
  Http.get
          {url = urlWithBase "/rest/gps/data?last=10"
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

getGpsStatus : (Cmd Msg)
getGpsStatus =
  Http.get
          {url = urlWithBase "/rest/gps/status"
          , expect = Http.expectJson GpsStatus (field "status" string)
          }


getTracks : (Cmd Msg)
getTracks =
  Http.request
          {
          method = "GET"
        , headers = [Http.header "Accept" "application/json"]
        , url = urlWithBase "/rest/gps/data/tracks?last=10"
        , body = Http.emptyBody
        , expect = Http.expectJson NewTracks (Json.Decode.list string)
        , timeout = Nothing
        , tracker = Nothing
          }

deleteTrack : Maybe String -> (Cmd Msg)
deleteTrack mid =
  case mid of 
    Nothing -> Cmd.none
    Just id -> 
       Http.request
        { method = "DELETE"
        , headers = []
        , url = urlWithBase "/rest/gps/data/tracks/" ++ id
        , body = Http.emptyBody
        , expect = Http.expectString TrackDeleted
        , timeout = Nothing
        , tracker = Nothing
        } 

-- SUBSCRIPTIONS
subscriptions : Model -> Sub Msg
subscriptions model =
--      Sub.none
--   Time.every 1000 Tick
      Sub.batch [
        onMessage OnMessage
      ]

-- VIEW

view : Model -> Html Msg
view model =
    div [] [
      h1 [align "center"][text "GPS Tracker"]
      ,button [ onClick (viewMsg model)
              ,style "background-color" (viewColor model)
              ,style "font-size" "5em"
              ] 
             [ text (viewCommandText model) ]
      ,div [] []
      ,h3 [] [text "Current Speed"]
      ,viewSpeed model
      ,div [] []
      ,h3 [] [text "Recent Locations"]
      ,viewLocations model
      ,h3 [] [text "Recent Tracks"]
      ,viewTracks model
      ,button [ onClick Slower
               ,style "font-size" "5em"
               ] [text "slower"]
      , text Html.Entity.nbsp
      ,button [ onClick Faster
               ,style "font-size" "5em"
              ] [text "faster"]
    ]

viewLocations : Model -> Html Msg
viewLocations model = 
  case model.locations of 
    Ok ls -> ul [] (List.map (\s -> (li [] [text (convertTpvReportToString s)])) ls)
    Err msg -> viewErrorText msg

convertTpvReportToString: TpvReport -> String
convertTpvReportToString tpv =
  convertMaybeStringToString tpv.time 
  ++ ": "
  ++ "long = "++convertFloatToString tpv.longitude 
  ++ ", "
  ++ "lat = "++convertFloatToString tpv.latitude
  ++", "
  ++ "alt = "++convertFloatToString tpv.altitude
  ++", "
  ++ "speed (m/s) = "++convertFloatToString tpv.speed

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

viewTracks : Model -> Html Msg
viewTracks model = 
  case model.tracks of 
    Ok ls -> Html.Keyed.ul [] (List.map (\id -> (id,li [] (createLink model id))) ls)
    Err msg -> viewErrorText msg

viewSpeed : Model -> Html Msg
viewSpeed model = 
  let 
    stle = div [style "font-size" "3em"]
  in
  case model.speed of 
    Nothing -> stle [text ""]
    Just speed -> stle [text ((speedInKmph speed)++" km/h") ]

speedInKmph speed =
      String.fromFloat (speed / 1000 * 60*60)

viewErrorText msg =
  div [style "color" "red"] [text (getErrorText msg)]

viewMsg : Model -> Msg
viewMsg model =
  case model.status of 
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

viewCommandText : Model -> String
viewCommandText model =
  case model.status of 
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

viewColor : Model -> String
viewColor model =
  case model.status of 
    Starting -> "green"
    Stopping -> "blue"
    Started r ->
      case r of
        Ok _ -> "blue"
        Err  _ -> "red"
    Stopped r ->
      case r of
        Ok  _ -> "green"
        Err _ -> "red"

getErrorText : Http.Error -> String
getErrorText r =
  case r of 
    BadUrl str -> "Bad URL: " ++ str
    Timeout -> "Timeout"
    NetworkError -> "Network Error"
    BadStatus i -> "Status " ++ String.fromInt i
    BadBody str -> "Body " ++ str

createLink : Model -> String -> List (Html Msg)
createLink model id =
  [a [href (urlWithBase ("/rest/gps/data/tracks/kml/" ++ id ++ ".kml")), target "_blank"] [text id]
  ,text Html.Entity.nbsp
  ,text "("
  ,a [href (urlWithBase ("/rest/gps/data/tracks/csv/" ++ id ++ ".csv")), target "_blank"] [text "csv"]
  ,text ")"
  ,text Html.Entity.nbsp
  ,deleteButtonPossibleConfirmation model id]

deleteButtonPossibleConfirmation model id =
  let 
    deleteButton = button [onClick (Delete id)] [div [style "color" "red"] [b [] [text Html.Entity.cross]]]
  in 
    case model.delete of 
      Nothing -> deleteButton 
      Just did -> 
       if did == id then 
        div [style "color" "red"] [b [] [text "Really delete?"], button [ onClick DeleteConfirmed] [text "Yes"], button [onClick DeleteCancelled] [text "No"]]
       else
        deleteButton


    
