# 🧠 Function Calling Example

Here’s an example of a supported `json` file:

```json
[
  {
    "match": [
      "hello",
      "hi",
      "hey",
      "greetings",
      "yo",
      "sup"
    ],
    "response": "Hello {player_name}! 👋 How can I help you today?"
  }
]
```

If the player says `Hello`, the plugin will only send the matched line:

```pgsql
player --> [You → AI]: Hello  
sent to AI --> [You → AI]: Hello Hello {player_name}! 👋 How can I help you today?
```

🔄 Only the matched message and response are sent — not the entire file.

# 🔧 Parameter Support

You can use the following dynamic placeholders in your responses.

<div align="center">

|Placeholder|Replaced With|
|-|-|
|`{player_name}`|Player’s in-game name|
|`{player_uuid}`|Player’s UUID|
|`{time_server}`|Current time of server |(local timezone)|
|`{time_utc}`|UTC time|
|`{time_utc_plus_00_00}`|UTC+00:00 formatted time|
|`{time_utc_minus_00_00}`|UTC-00:00 formatted time|
|`{time_gmt}`|GMT time|
|`{time_gmt_plus_00_00}`|GMT+00:00 formatted time|
|`{time_gmt_minus_00_00}`|GMT-00:00 formatted time|
|`{time_bangkok}`|Time in Asia/Bangkok|
|`{time_berlin}`|Time in Europe/Berlin|
|`{time_london}`|Time in Europe/London|
|`{time_los_angeles}`|Time in America/|Los_Angeles|
|`{time_new_york}`|Time in America/New_York|
|`{time_paris}`|Time in Europe/Paris|
|`{time_singapore}`|Time in Asia/Singapore|
|`{time_sydney}`|Time in Australia/Sydney|
|`{time_tokyo}`|Time in Asia/Tokyo|
|`{time_toronto}`|Time in America/Toronto|

</div>
