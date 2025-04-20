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

You can use the following dynamic placeholders in your responses:

`{player_name}` → Replaced with the player’s name

`{player_uuid}` → Replaced with the player’s UUID
