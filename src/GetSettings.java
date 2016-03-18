/*
Copyright 2016 the project authors as listed in the AUTHORS file.
All rights reserved. Use of this source code is governed by the
license that can be found in the LICENSE file.
*/

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class GetSettings
 */
@WebServlet("/GetSettings")
public class GetSettings extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetSettings() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		String resp = "{\"settings\":[\n";
		
		resp+="\"READ\": \""+Settings.ACTION_RATIOS[0]+"\",\n";
		resp+="\"WRITE\": \""+Settings.ACTION_RATIOS[1]+"\",\n";
		resp+="\"REFCHANGE\": \""+Settings.ACTION_RATIOS[2]+"\",\n";
		resp+="\"ALLOC\": \""+Settings.ACTION_RATIOS[3]+"\",\n";
		resp+="\"ADD\": \""+Settings.ACTION_RATIOS[4]+"\",\n";
		resp+="\"REMOVE\": \""+Settings.ACTION_RATIOS[5]+"\" ,";
		
		resp+="\"ACTIONS_PER_REQUEST\": \""+Settings.ACTIONS_PER_REQUEST+"\",\n";
		resp+="\"LOCAL_ACTION_RATIO\": \""+Settings.LOCAL_ACTION_RATIO+"\",\n";
		
		resp+="\"MIN_PAYLOAD_SIZE\": \""+Settings.MIN_PAYLOAD_SIZE+"\",\n";
		resp+="\"MED_PAYLOAD_SIZE\": \""+Settings.MED_PAYLOAD_SIZE+"\",\n";
		resp+="\"MAX_PAYLOAD_SIZE\": \""+Settings.MAX_PAYLOAD_SIZE+"\",\n";
		
		resp+="\"MIN_REFS\": \""+Settings.MIN_REFS+"\",\n";
		resp+="\"MED_REFS\": \""+Settings.MED_REFS+"\",\n";
		resp+="\"MAX_REFS\": \""+Settings.MAX_REFS+"\",\n";
		
		resp += "]}";
		
		
		response.getOutputStream().println(resp);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
