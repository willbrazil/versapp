package com.versapp.chat;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.net.URLDecoder;
import java.util.ArrayList;

/**
 * Created by william on 25/09/14.
 */
public class ChatDeserializer implements JsonDeserializer<Chat> {

    private static final String TYPE_JSON_KEY = "type";
    private static final String UUID_JSON_KEY = "uuid";
    private static final String CHAT_NAME_JSON_KEY = "name";

    // One to One specific
    private static final String IS_OWNER_JSON_KEY = "isOwner";

    // Thought specific
    private static final String DEGREE_JSON_KEY = "degree";
    private static final String CID_JSON_KEY = "cid";

    // Group chat specific
    private static final String PARTICIPANTS_JSON_KEY = "participants";
    private static final String OWNER_ID_JSON_KEY = "owner";

    private static final String CHAT_TYPE_ONE_TO_ONE = "121";
    private static final String CHAT_TYPE_THOUGHT = "thought";
    private static final String CHAT_TYPE_GROUP = "group";

    @Override
    public Chat deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

        Chat chat;

        final JsonObject jsonObject = json.getAsJsonObject();

        JsonElement typeElement = jsonObject.get(TYPE_JSON_KEY);
        JsonElement uuidElement = jsonObject.get(UUID_JSON_KEY);
        JsonElement nameElement = jsonObject.get(CHAT_NAME_JSON_KEY);

        String type = typeElement.getAsString();
        String uuid = uuidElement.getAsString();
        String name = nameElement.getAsString();

        if (type.equals(CHAT_TYPE_ONE_TO_ONE)){

            JsonElement isOwnerElement = jsonObject.get(IS_OWNER_JSON_KEY);

            boolean isOwner = isOwnerElement.getAsBoolean();

            chat = new OneToOneChat(uuid, name, isOwner);

        } else if(type.equals(CHAT_TYPE_THOUGHT)){

            JsonElement degreeElement = jsonObject.get(DEGREE_JSON_KEY);
            JsonElement cidElement = jsonObject.get(CID_JSON_KEY);

            int degree = degreeElement.getAsInt();
            long cid = cidElement.getAsLong();

            // name is url encoded when it's a thought.
            name = URLDecoder.decode(name);

            chat = new ConfessionChat(uuid, name, cid, degree);

        } else { // type: group

            JsonElement ownerIdElement = jsonObject.get(OWNER_ID_JSON_KEY);
            JsonElement participantsElement = jsonObject.get(PARTICIPANTS_JSON_KEY);

            String ownerId = ownerIdElement.getAsString();
            JsonArray participantsJsonArray = participantsElement.getAsJsonArray();
            String[] parts = new String[participantsJsonArray.size()];
            for (int i = 0; i < participantsJsonArray.size(); i++){
                parts[i] = participantsJsonArray.get(i).getAsString();
            }

            ArrayList<Participant> participants = new ArrayList<Participant>();
            for (String s : parts){
                participants.add(new Participant(s.trim(), s.trim() + " name"));
            }


            chat = new GroupChat(uuid, name, ownerId, participants);

        }

        return chat;
    }
}
