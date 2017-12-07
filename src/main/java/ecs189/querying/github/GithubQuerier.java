package ecs189.querying.github;

import ecs189.querying.Util;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Vincent on 10/1/2017.
 */
public class GithubQuerier {

    private static final String BASE_URL = "https://api.github.com/users/";

    public static String eventsAsHTML(String user) throws IOException, ParseException {
        List<JSONObject> response = getEvents(user);
        StringBuilder sb = new StringBuilder();
        sb.append("<div>");
        for (int i = 0; i < response.size(); i++) {
            JSONObject event = response.get(i);
            // Get event type
            String type = event.getString("type");
            // Get created_at date, and format it in a more pleasant style
            String creationDate = event.getString("created_at");
            SimpleDateFormat inFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z'");
            SimpleDateFormat outFormat = new SimpleDateFormat("dd MMM, yyyy");
            Date date = inFormat.parse(creationDate);
            String formatted = outFormat.format(date);

            // Get Commits
            JSONObject payload = event.getJSONObject("payload");
            JSONArray commits = payload.getJSONArray("commits");


            // Add type of event as header
            sb.append("<h3 class=\"type\">");
            sb.append(type);
            sb.append("</h3>");
            // Add formatted date
            sb.append(" on ");
            sb.append(formatted);
            sb.append("<br />");
            // Add collapsible JSON textbox (don't worry about this for the homework; it's just a nice CSS thing I like)
            sb.append("<a data-toggle=\"collapse\" href=\"#event-" + i + "\">JSON</a>");
            sb.append("<div id=event-" + i + " class=\"collapse\" style=\"height: auto;\"> <pre>");
            sb.append(event.toString());
            sb.append("</pre> </div>");

            // Show Commits Vertically
            for(int j=0; j < commits.length(); j++) {
                JSONObject commit = commits.getJSONObject(j);
                // Get the sha id
                String sha = commit.getString("sha");
                String message = commit.getString("message");
                sb.append(" <ul>");
                sb.append("sha: " + sha);
                sb.append(" </ul>");
                sb.append(" <ul> ");
                sb.append("commit: "+ message);
                sb.append(" </ul>");

            }
        }
        sb.append("</div>");
        return sb.toString();
    }

    private static List<JSONObject> getEvents(String user) throws IOException {
        List<JSONObject> eventList = new ArrayList<JSONObject>();
        int page = 0;
        while(page < 11) { // Max pagination allows us to access
            // Create the url
            String url = BASE_URL + user + "/events?page=" + page;
            System.out.println(url);

            // Make the query
            JSONObject json = Util.queryAPI(new URL(url));
            System.out.println(json);

            // format the return value
            JSONArray events = json.getJSONArray("root");
            if(events.length() == 0) return eventList;

            for (int i = 0; i < events.length(); i++) {
                // Get the event
                JSONObject event = events.getJSONObject(i);
                // convert to string
                String type = event.getString("type");
                if (type.equals("PushEvent")) {
                    eventList.add(events.getJSONObject(i));
                }
                // We've got our data
                if (eventList.size() == 10) return eventList;
            }
            page++;
        }
        return eventList;
    }
}