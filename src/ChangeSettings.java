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
 * Servlet implementation class ChangeSettings
 */
@WebServlet("/ChangeSettings")
public class ChangeSettings extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ChangeSettings() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		if(request.getParameter("READ")!=null){
			Settings.ACTION_RATIOS[0] = Double.parseDouble(request.getParameter("READ"));
		}
		
		if(request.getParameter("WRITE")!=null){
			Settings.ACTION_RATIOS[1] = Double.parseDouble(request.getParameter("WRITE"));
		}
		
		if(request.getParameter("REFCHANGE")!=null){
			Settings.ACTION_RATIOS[2] = Double.parseDouble(request.getParameter("REFCHANGE"));
		}
		
		if(request.getParameter("ALLOC")!=null){
			Settings.ACTION_RATIOS[3] = Double.parseDouble(request.getParameter("ALLOC"));
		}
		
		if(request.getParameter("ADD")!=null){
			Settings.ACTION_RATIOS[4] = Double.parseDouble(request.getParameter("ADD"));
		}
		
		if(request.getParameter("REMOVE")!=null){
			Settings.ACTION_RATIOS[5] = Double.parseDouble(request.getParameter("REMOVE"));
		}
		
		
		if(request.getParameter("ACTIONS_PER_REQUEST")!=null){
			Settings.ACTIONS_PER_REQUEST = Integer.parseInt(request.getParameter("ACTIONS_PER_REQUEST"));
		}
		
		if(request.getParameter("LOCAL_ACTION_RATIO")!=null){
			Settings.LOCAL_ACTION_RATIO = Double.parseDouble(request.getParameter("LOCAL_ACTION_RATIO"));
		}
		
		
		if(request.getParameter("MIN_PAYLOAD_SIZE")!=null){
			Settings.MIN_PAYLOAD_SIZE = Integer.parseInt(request.getParameter("MIN_PAYLOAD_SIZE"));
		}
		
		if(request.getParameter("MAX_PAYLOAD_SIZE")!=null){
			Settings.MAX_PAYLOAD_SIZE = Integer.parseInt(request.getParameter("MAX_PAYLOAD_SIZE"));
		}
		
		if(request.getParameter("MED_PAYLOAD_SIZE")!=null){
			Settings.MED_PAYLOAD_SIZE = Integer.parseInt(request.getParameter("MED_PAYLOAD_SIZE"));
		}
		
		
		if(request.getParameter("MIN_REFS")!=null){
			Settings.MIN_REFS = Integer.parseInt(request.getParameter("MIN_REFS"));
		}
		
		if(request.getParameter("MAX_REFS")!=null){
			Settings.MAX_REFS = Integer.parseInt(request.getParameter("MAX_REFS"));
		}
		
		if(request.getParameter("MED_REFS")!=null){
			Settings.MED_REFS = Integer.parseInt(request.getParameter("MED_REFS"));
		}
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
