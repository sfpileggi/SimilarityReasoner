/*******************************************************************************

    Copyright (C) 2013  Salvatore Flavio Pileggi

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as
    published by the Free Software Foundation, either version 3 of the
    License, or (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.

********************************************************************************/



package reasoner;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.sparql.resultset.ResultSetMem;

import data.FullRS;
import data.SFactorRS;

abstract class SPARQLonSimilarity implements similarityInterface{
	
	OntModel ontology;
	OntModel similarity;
	OntModel values;
	
	protected String similarityPrefix;
	
	ResultSetMem deterministic;
	
	private float num_determisistic; //deterministic knowledge involving similarities
	private float added;             //uncertain knowledge added
	
	List<Statement> statsToAdd;
	
    OntModel complete;
    
    public SPARQLonSimilarity(String similarityPrefix){
    	
    	this.similarityPrefix = similarityPrefix;
    }
    

	@Override
	public SFactorRS QueryOnSimilarity(String ontologyFile, String similarityFile, String SPARQLQuery) {
		
		ontology = ModelFactory.createOntologyModel( OntModelSpec.OWL_MEM_MICRO_RULE_INF);
        ontology.read( ontologyFile, "RDF/XML" );
        
        similarity = ModelFactory.createOntologyModel( OntModelSpec.OWL_MEM_MICRO_RULE_INF);
        similarity.read( similarityFile, "RDF/XML" );
		
	    return QueryOnSimilarity(ontology, similarity, SPARQLQuery);
	}
	
	
	@Override
    public SFactorRS QueryOnSimilarity(OntModel ontology, OntModel similarity, String SPARQLQuery) {
    	
		//return type
		SFactorRS RS = new SFactorRS();
		
		set_n_deterministic(ontology);
		
		getDeterministicResults(RS, SPARQLQuery);
		getUncertainResults(RS);
		getOverallResults(RS, SPARQLQuery);
		
    	return RS;
    }
	
	
	private OntModel getDeterministicResults(FullRS RS, String SPARQLQuery){
		
        //execute query
        com.hp.hpl.jena.query.Query query = QueryFactory.create(SPARQLQuery); 
        QueryExecution qe = QueryExecutionFactory.create(query,ontology);
        ResultSet results =  qe.execSelect();
        
       //Set deterministic result set
        deterministic = new ResultSetMem(results);
        RS.setDeterministic(deterministic);
	
        return ontology;
	}
	
	
	
	protected void setUncertainResult(SFactorRS RS, ResultSetMem complete){
			
		RS.setUncertain(complete);
		RS.setSimilaityFactor(this.getSimilarityFactor());
	}
	
	
	
	abstract void getUncertainResults(SFactorRS RS);
	
	//abstract void getOverallResults(SFactorRS RS, String SPARQLQuery);
	

	public void set_n_deterministic(OntModel ontology){
		
		int result=0;
		
		String prefix = ontology.getNsPrefixURI("");
		Iterator i = ontology.listStatements();
		
		while(i.hasNext()){
			String curr = i.next().toString();
			if(curr.contains(prefix)){
				result++;
			}
		}
		
		this.num_determisistic = result;
	}
	
    public void set_n_added(float added){
		
		this.added = added;
	}
    
    public float getSimilarityFactor(){
    	
    	float similarityFactor;
    	similarityFactor = this.num_determisistic / (this.num_determisistic+this.added);

    	return similarityFactor;
    }
	

	protected List<String> getElement(String element){
		  
		List<String> res=new ArrayList<String>();
		
		String queryString =        
		           // "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "+
		           // "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>  "+
		            "select ?e "+
		            "where { "+ "<"+element+">" +
		             " <"+this.similarityPrefix+"similarTo> ?e  "+
		            "} \n ";
	        
	        com.hp.hpl.jena.query.Query query = QueryFactory.create(queryString); 
	        QueryExecution qe = QueryExecutionFactory.create(query, similarity);
	        com.hp.hpl.jena.query.ResultSet results =  qe.execSelect();
	        
	        while(results.hasNext()){
	        	
	        	String toAdd = results.next().toString();
	        	
	        	toAdd = toAdd.substring((toAdd.indexOf("<")+1),toAdd.indexOf(">"));
	            if(!res.contains(toAdd)) {
	        			res.add(toAdd);
	        			//System.err.println(toAdd);
	        	}
	        	
	        }
	      
	        return res;
	}
	
	
	
	protected void getOverallResults(SFactorRS RS, String SPARQLQuery){
		
        
		OntModel union = ModelFactory.createOntologyModel( OntModelSpec.OWL_MEM_MICRO_RULE_INF);
		
		union.add(this.complete);
		union.add(this.ontology);
		
		getSimOnValue();
		
	/*	Iterator i = values.listStatements();
		while(i.hasNext()){
			System.err.println(i.next().toString());
		}*/
		
		union.add(this.values);
		
		//execute query
        com.hp.hpl.jena.query.Query queryFinal = QueryFactory.create(SPARQLQuery); 
        QueryExecution qe1 = QueryExecutionFactory.create(queryFinal,union);
        com.hp.hpl.jena.query.ResultSet resultsFinal =  qe1.execSelect();
        
        ResultSetMem rsfinal = new ResultSetMem(resultsFinal);
        
        this.setUncertainResult(RS, rsfinal);
       
	}
    
    private void getSimOnValue(){
    	values = ModelFactory.createOntologyModel( OntModelSpec.OWL_MEM_MICRO_RULE_INF);
    	System.out.println("\nSimilarity-on-value:");
    	
    	String queryString =        
	            "select ?sub ?obj "+
	            "where { "+
	             "?sub <"+this.similarityPrefix+"onValue> ?obj."+
	             "?sub a <"+this.similarityPrefix+"uncertainElement>." +
	            "} \n ";
    	
    	com.hp.hpl.jena.query.Query query = QueryFactory.create(queryString); 
        QueryExecution qe = QueryExecutionFactory.create(query, similarity);
        com.hp.hpl.jena.query.ResultSet results =  qe.execSelect();
        
        Resource subject = null;
		Property predicate = null;
		RDFNode object = null;
		Statement stat = null;
                
        while(results.hasNext()){
        	
        	String s = results.next().toString();
        	
        	String s1 = s.substring(0,s.indexOf("> ) "));
        	s1 = s1.substring(s1.indexOf("<")+1);
        	
        	String s2 = s.substring(s.indexOf("> ) "));
        	s2 = s2.substring(s2.indexOf("\"")+1,s2.length()-2);
        	s2 = s2.substring(0,s2.indexOf("\""));
        	s2 = s2.replace("\\s+","");
        	String s21 = s2.substring(0,s2.indexOf(","));
        	String s22 = s2.substring(s2.indexOf(",")+1);
        	
        	subject = ResourceFactory.createResource(s1);
       	    predicate = ResourceFactory.createProperty(s21);
        	object = ResourceFactory.createResource(s22);
        	System.out.println("["+subject.toString()+", "+predicate.toString()+", "+object.toString()+"]");
        	stat = ResourceFactory.createStatement(subject, predicate, object);
        	
        	this.values.add(stat);
        	//System.err.println(s1+ " "+s21+" "+s22);
        	
        }
        
    }
    
    
    
    
}
