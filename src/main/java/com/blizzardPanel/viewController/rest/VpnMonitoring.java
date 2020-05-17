package com.blizzardPanel.viewController.rest;

import com.blizzardPanel.DataException;
import com.blizzardPanel.Logs;
import com.blizzardPanel.User;
import com.blizzardPanel.dbConnect.DBLoadObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Path("/vpn")
public class VpnMonitoring {

    @POST
    @Path("/monitoring/{user_ip}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response monitoring(
            @PathParam("user_ip") String userIp,
            String data
    ) {
        JsonObject jInfo = JsonParser.parseString(data).getAsJsonObject();
        User user = new User.Builder(jInfo.get("ip").getAsString(), true).build();

        try {
            JsonArray vpn_monitoring = DBLoadObject.dbConnect.select(
                    User.TABLE_NAME_VPN,
                    new String[]{"*"},
                    "user_id=? AND timestamp=?",
                    new String[]{user.getId()+"", jInfo.get("start_date").getAsLong()*1000+""},
                    true
            );

            // Prepare values
            List<Object> columns = new ArrayList<>();
            List<Object> values = new ArrayList<>();

            if (vpn_monitoring.size() > 0) { // Update
                JsonObject vpnCurrentInf = vpn_monitoring.get(0).getAsJsonObject();

                int byte_sent = jInfo.get("bytes_sent").getAsInt() + vpnCurrentInf.get("t_bytes_sent").getAsInt();
                int bytes_recv = jInfo.get("bytes_recv").getAsInt() + vpnCurrentInf.get("t_bytes_recv").getAsInt();
                int packets_sent = jInfo.get("packets_sent").getAsInt() + vpnCurrentInf.get("t_packets_sent").getAsInt();
                int packets_recv = jInfo.get("packets_recv").getAsInt() + vpnCurrentInf.get("t_packets_recv").getAsInt();
                int errin = jInfo.get("errin").getAsInt() + vpnCurrentInf.get("t_errin").getAsInt();
                int errout = jInfo.get("errout").getAsInt() + vpnCurrentInf.get("t_errout").getAsInt();
                int dropin = jInfo.get("dropin").getAsInt() + vpnCurrentInf.get("t_dropin").getAsInt();
                int dropout = jInfo.get("dropout").getAsInt() + vpnCurrentInf.get("t_dropout").getAsInt();

                columns.add("t_bytes_sent");
                values.add(byte_sent);
                columns.add("t_bytes_recv");
                values.add(bytes_recv);
                columns.add("t_packets_sent");
                values.add(packets_sent);
                columns.add("t_packets_recv");
                values.add(packets_recv);
                columns.add("t_errin");
                values.add(errin);
                columns.add("t_errout");
                values.add(errout);
                columns.add("t_dropin");
                values.add(dropin);
                columns.add("t_dropout");
                values.add(dropout);

                DBLoadObject.dbConnect.update(
                        User.TABLE_NAME_VPN,
                        columns,
                        values,
                        "user_id=? AND timestamp=?",
                        new String[]{user.getId()+"", jInfo.get("start_date").getAsLong()*1000+""}
                );
                Logs.infoLog(this.getClass(), "VPN Monitoring update! - "+ user.getBattle_tag());
            } else { // Insert
                columns.add("user_id");
                values.add(user.getId());
                columns.add("timestamp");
                values.add(jInfo.get("start_date").getAsLong()*1000);
                columns.add("t_bytes_sent");
                values.add(jInfo.get("bytes_sent").getAsString());
                columns.add("t_bytes_recv");
                values.add(jInfo.get("bytes_recv").getAsString());
                columns.add("t_packets_sent");
                values.add(jInfo.get("packets_sent").getAsString());
                columns.add("t_packets_recv");
                values.add(jInfo.get("packets_recv").getAsString());
                columns.add("t_errin");
                values.add(jInfo.get("errin").getAsString());
                columns.add("t_errout");
                values.add(jInfo.get("errout").getAsString());
                columns.add("t_dropin");
                values.add(jInfo.get("dropin").getAsString());
                columns.add("t_dropout");
                values.add(jInfo.get("dropout").getAsString());

                DBLoadObject.dbConnect.insert(
                        User.TABLE_NAME_VPN,
                        User.TABLE_KEY_VPN,
                        columns,
                        values
                );
                Logs.infoLog(this.getClass(), "VPN Monitoring create for user "+ user.getBattle_tag());
            }
            return Response.ok().build();
        } catch (DataException | SQLException e) {
            Logs.fatalLog(this.getClass(), "FAILED to save vpn monitoring info! "+ e);
        }

        return Response.serverError().build();
    }

    @PUT
    @Path("/disconnect/{user_ip}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response disconnect(
            @PathParam("user_ip") String userIp,
            String data
    ) {
        JsonObject jInfo = JsonParser.parseString(data).getAsJsonObject();
        User user = new User.Builder(userIp, true).build();
        try {
            DBLoadObject.dbConnect.update(
                    User.TABLE_NAME_VPN,
                    new String[]{"timestamp_dc"},
                    new String[]{new Date().getTime()+""},
                    "user_id=? AND timestamp=?",
                    new String[]{user.getId()+"", jInfo.get("start_date").getAsLong()*1000+""}
            );
            return Response.ok().build();
        } catch (DataException | SQLException e) {
            Logs.fatalLog(this.getClass(), "FAILED to save disconnect for user "+ user.getBattle_tag() +" -- "+ e);
        }
        return Response.serverError().build();
    }
}
