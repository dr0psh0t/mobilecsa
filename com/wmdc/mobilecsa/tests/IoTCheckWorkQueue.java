package wmdc.mobilecsa.tests;

import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/IoTCheckWorkQueue")
public class IoTCheckWorkQueue extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        JSONObject resJson = new JSONObject();

        resJson.put("joInfo", "25259-96884");
        resJson.put("success", 1);

        resJson.put("dTime", 1601633712);

        resJson.put("mId", 100060);
        resJson.put("startTime", 1599494016);
        resJson.put("hash", "sgkdfkjt45-2340-8gasdf|fjsdkflksj==");

        //resJson.put("success", 0);
        //resJson.put("reason", "No Work in Queue, or already completed.");

        out.print(resJson);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);
    }
}
/*

Feeling my way through the darkness
Guided by a beating heart
I can't tell where the journey will end
But I know where to start
They tell me I'm too young to understand
They say I'm caught up in a dream
Well life will pass me by if I don't open up my eyes
Well that's fine by me
So wake me up when it's all over
When I'm wiser and I'm older
All this time I was finding myself
And I didn't know﻿ I was lost
So wake me up when it's all over
When I'm wiser and I'm older
All this time I was finding myself
And I didn't know﻿ I was lost
I tried carrying the weight of the world
But I only have two hands
I hope I get the chance to travel the world
But I don't have any plans
I wish that I could stay forever this young
Not afraid to close my eyes
Life's a game﻿ made for everyone
And love is the prize
So wake me up when it's all over
When I'm wiser and I'm older
All this time I was finding myself
And I didn't know﻿ I was lost
So wake me up when it's all over
When I'm wiser and I'm older
All this time I was finding myself
And I didn't know﻿ I was lost
I didn't know I was lost
I didn't know I was lost
I didn't know I was lost
I didn't know I was lost
So wake me up when it's all over
When I'm wiser and I'm older
All this time I was finding myself
And I didn't know﻿ I was lost

 */