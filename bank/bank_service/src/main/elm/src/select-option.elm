port module Main exposing (main)

import Html exposing (..)
import Html.Events exposing (..)
import Html.Attributes exposing (..)
import String exposing (fromFloat)
import Browser

main =
  Browser.element
    { init = init
    , update = update
    , subscriptions = subscriptions
    , view = view
    }

-- MODEL

type alias Model =
  { options : List String
    , selection : Maybe String
  }

nothingString = "-- nothing --"

init : () -> (Model, Cmd Msg) 
init _ = 
  ( { options = [nothingString, "user1", "user2", "user3", "user4"]
     , selection = Nothing }
    , Cmd.none
  )

-- UPDATE

type Msg 
  = Selected String

update : Msg -> Model -> (Model,Cmd Msg)
update msg model =
  case msg of 
    Selected s -> ({ model | selection = (selectionFromString s)}, Cmd.none)


selectionFromString s =
  if s == nothingString then
    Nothing 
  else
    Just s

-- COMMANDS

-- SUBSCRIPTIONS
subscriptions : Model -> Sub Msg
subscriptions model =
    Sub.none

-- VIEW

view : Model -> Html Msg
view model =
    div [] [
      h1 [align "center"][text "Select and Option example"]
      ,selectHtmlElement model
      ,div [] [br [][]]
      ,text ("selected value: " ++ (toString model.selection))
    ]

selectHtmlElement model =
    select [onInput Selected] (List.map optionElement model.options)

optionElement v =
    option [value v] [text v]

toString maybe =
  case maybe of 
    Nothing -> "nothing"
    Just s -> s
