package io.github.mcengine.api.artificialintelligence.model;

import io.github.mcengine.api.artificialintelligence.IMCEngineArtificialIntelligenceApi;
import io.github.mcengine.api.artificialintelligence.util.MCEngineArtificialIntelligenceApiUtil;
import org.bukkit.plugin.Plugin;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;

/**
 * OpenAI API implementation of {@link IMCEngineArtificialIntelligenceApi}.
 * Communicates with the OpenAI Chat API using the configured model and token.
 */
public class MCEngineArtificialIntelligenceApiOpenAi implements IMCEngineArtificialIntelligenceApi {

    private final String token;
    private final String aiModel;
    private final MCEngineArtificialIntelligenceApiUtil logger;

    public MCEngineArtificialIntelligenceApiOpenAi(Plugin plugin) {
        this.token = plugin.getConfig().getString("ai.openai.token", null);
        this.aiModel = plugin.getConfig().getString("ai.openai.model", "gpt-3.5-turbo");
        this.logger = new MCEngineArtificialIntelligenceApiUtil(plugin.getLogger());
        logger.info("Using OpenAI model: " + aiModel);
    }

    @Override
    public String getResponse(String message) {
        try {
            URI uri = URI.create("https://api.openai.com/v1/chat/completions");
            HttpURLConnection conn = (HttpURLConnection) uri.toURL().openConnection();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer " + token);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            JSONObject payload = new JSONObject();
            payload.put("model", aiModel);
            payload.put("temperature", 0.7);

            JSONArray messages = new JSONArray();
            JSONObject userMessage = new JSONObject();
            userMessage.put("role", "user");
            userMessage.put("content", message);
            messages.put(userMessage);
            payload.put("messages", messages);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(payload.toString().getBytes("utf-8"));
            }

            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                logger.warn("OpenAI API returned status: " + responseCode);
                return "Error: OpenAI API request failed.";
            }

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder responseBuilder = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                responseBuilder.append(line);
            }
            in.close();

            JSONObject responseJson = new JSONObject(responseBuilder.toString());
            JSONArray choices = responseJson.getJSONArray("choices");

            if (choices.length() > 0) {
                JSONObject messageObj = choices.getJSONObject(0).getJSONObject("message");
                return messageObj.getString("content").trim();
            }

            return "No response from OpenAI.";
        } catch (Exception e) {
            logger.error("OpenAI API error: " + e.getMessage());
            return "Exception: " + e.getMessage();
        }
    }
}
