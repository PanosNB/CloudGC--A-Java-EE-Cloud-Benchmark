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
 * Servlet implementation class GraphAction
 */
@WebServlet("/GraphAction")
public class GraphAction extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	public static final Graph globalGraph = new Graph(true); 
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GraphAction() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		Graph localGraph = new Graph(false);
		
		int allocs = Settings.getIntProperty("INIT_ALLOCS");
		int refChanges = Settings.getIntProperty("INIT_REF_CHANGES");
		
		localGraph.emptyAllAndGC();
		Distribution.reSeed();
		
		for(int i = 0; i < allocs; i++){
			//if(Distribution.randU() < Settings.getDoubleProperty("LOCAL_ACTION_RATIO")){
				localGraph.allocate();
			//} else {
			//	globalGraph.allocate();
			//}
		}
				
		for(int i = 0; i < refChanges; i++){
			localGraph.changeRef();
		}
		
		for(int i = 0; i < Settings.getIntProperty("ACTIONS_PER_REQUEST"); i++){
			if(Distribution.randU() < Settings.getDoubleProperty("LOCAL_ACTION_RATIO")){
				localGraph.doRandAction(response.getWriter());
			} else {
				globalGraph.doRandAction(response.getWriter());
			}
		}
		localGraph.empty();
		Runtime runtime = Runtime.getRuntime();
		response.getWriter().append("" + (System.currentTimeMillis())+"\n");
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
