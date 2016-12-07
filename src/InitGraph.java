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
 * Servlet implementation class InitGraph
 */
@WebServlet("/InitGraph")
public class InitGraph extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public InitGraph() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		int allocs = Settings.getIntProperty("INIT_ALLOCS");
		int refChanges = Settings.getIntProperty("INIT_REF_CHANGES");
		Distribution dist = new Distribution();
		
		GraphAction.globalGraph.emptyAllAndGC();
		
		for(int i = 0; i < allocs; i++){
			GraphAction.globalGraph.allocate(dist);
		}
				
		for(int i = 0; i < refChanges; i++){
			GraphAction.globalGraph.changeRef(dist);
		}
		
		
		Runtime runtime = Runtime.getRuntime();
		response.getWriter().append("" + (runtime.totalMemory() - runtime.freeMemory())+"\n");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
