package com.wp.mag;

import java.util.Iterator;

import javax.jcr.Node;
import javax.jcr.Repository;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import javax.jcr.Workspace;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;

import org.apache.jackrabbit.commons.JcrUtils;

public class SecuredRepository {
	
	public static void main(String[] args) throws Exception {
		Repository repository = JcrUtils.getRepository();
        Session session = repository.login( 
        new SimpleCredentials("admin", "admin".toCharArray()));
        
        try {
        	Workspace workspace = session.getWorkspace();
        	String[] workspaceNames =  workspace.getAccessibleWorkspaceNames();
        	for(String wspace : workspaceNames) {
        		System.out.println(wspace);
        	}
        	
        	Session session1 = repository.login( 
        	        new SimpleCredentials("admin", "admin".toCharArray()), "default");
        	System.out.println("workspace name:" + session1.getWorkspace().getName());
        	
        	
//        	QueryManager qm = session1.getWorkspace().getQueryManager();
//        	Query query = qm.createQuery("select * from [mgnl:page] as a where isdescendantnode (a, '/home')", Query.JCR_SQL2);
//        	QueryResult rs = query.execute();
//        	Iterable<Node> iterable = JcrUtils.getNodes(rs);
//        	Iterator nodeIter = iterable.iterator();
        	
        	
//        	Node r = session.getRootNode();
//        	Node node = session1.getNode("/home/knowledge");
//        	System.out.println(node.getName());
//        	NodeIterator nodeIter = root.getNodes();
//        	
//        	while(nodeIter.hasNext()) {
//        		Node n = (Node)nodeIter.next();
//        		System.out.println(n.getName());
//        	}
        	
        	session1.logout();
        	
        } finally { 
            session.logout();
        }
	 
	}

}