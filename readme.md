# For Danny, and the Future YFNMFCC

This is gonna be awesome.
It lives [here](https://github.com/sherbondy/pikarush)

1. Install [leiningen](http://leiningen.org/)
2. From the command line, do: `lein run`

You are done. That was awesome.

If you want to mix things up, look at the guts of:

1. `src/pikarush/calendar.clj` to change how calendars/wiki text is emitted.
2. `src-cljs/pikarush/main.cljs` to mess with the website script!

Calendar events are output to `events/cals`.

To change the event listing, make a new .edn file in the same format
as the others. Use `events/spring2014.edn` as a template.
Adhere to it rigorously. Or else.

To spit out a new version of the js, do:

  ```
  lein cljsbuild [once|auto]
  ```

`auto` lets you change things and have the results recompile automatically!
Hooray!
