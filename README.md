# Role Assigner Bot

A Discord bot that assigns roles based on the reactions to a particular message, and greets members if mentioned in a message, because why not?

## Usage

Before using the bot, 'config.edn' file should be created and filled in according to the 'config_template.edn' file. To easily fill in the blanks, enable the Discord Developer mode in User settings -> Appearance.

And don't forget to give the bot a role with the right to assign and remove roles!

Right now the bot is configured in such a way, that if a person removes their reaction, they will also lose the corresponding role. If they react more than once, however, without removing any of their previous reactions, they will have all the roles they were assigned.

Ok, have fun, bye!

## License

This program is in public domain. Use it however.
