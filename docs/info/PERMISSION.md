# 🔐 Permissions

This plugin supports permission-based access for managing AI conversations and plugin behavior. Below is the list of available permissions:

🌐 `mcengine.artificialintelligence.*`
- Description: Grants full access to all AI-related commands.

- Includes:

    - ✅ `mcengine.artificialintelligence.use`

    - 🔄 `mcengine.artificialintelligence.reload`

Recommended for: Admins or power users who need full control.

## 💬 `mcengine.artificialintelligence.use`
Description: Allows players to initiate a conversation with the AI using the `/ai` command.

- Default: `op`

Use case: Let players talk to the AI with natural messages after typing `/ai`.

## 🔁 `mcengine.artificialintelligence.reload`
Description: Allows reloading AI configuration without restarting the server using `/ai reload`.

- Default: `op`

Use case: Useful for admins to update function rules or reload changes on the fly.
