# starships
 Star Wars starships

- The data is pulled from server by using Retrofit/RxJava.
- StartsView class is from:
  https://gist.github.com/sofakingforever/24507173b7743784303ea1bbf8e9e6bb
- Starship details is serialized to Ship model class.
- AppSettings class is used to store ordering and favourite settings of the ship in SharedPreferences.
- Based on the ship class, the app will show the approriate image, if the ship class is not defined, Unknown image will be shown.
- Top right corner is menu button, user can re-order the ships by length, passenger number or rating.
- There's a checkbox on top right of each ship so that user can select it as favourite ship.
- When user taps on the ship, the app will zoom in that ship and show the details (name, model, mft., length, speed etc.), user can also set it as favourite ship in details panel.
- Tap Close to dismiss details panel and back to colletion screen.
- The app can load all ships from API  by using RestApiFacade.instance.fetchAllShips.

