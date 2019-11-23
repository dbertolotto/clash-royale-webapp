# clash

Application based on the [Clash Royale API](https://developer.clashroyale.com/) to check clan members, wars and war logs.
Hardwired to my current clan.

## Variables

You need some variable for set up. You can save them in the `.env` file and `source` it as needed.
* the API token set up in the variable `API_TOKEN`.
* If you have a proxy you can set it with the variable `FIXIE_URL`.
* You also need a `BASE_URL` for the correct rendering of the links in the pages.

## Heroku

* Load repo to Heroku and start app `git push heroku master`
* See variables `heroku config`
* Ensure one app is running `heroku ps:scale web=1`
* Check logs `heroku logs --tail`
See also [the heroku website](https://devcenter.heroku.com/articles/getting-started-with-clojure)

### Fixie
The project uses a proxy addon from heroku: fixie.
Call logs are [here](https://dashboard.usefixie.com/#/logs).

## License

Copyright © 2019 Davide Bertolotto

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
