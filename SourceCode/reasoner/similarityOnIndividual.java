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




/**********************
 By using this class, all similarities are processed as full similarities (similarity-on-individual) even though they are defined according to other models 
 ***********************/


package reasoner;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.sparql.resultset.ResultSetMem;


import data.SFactorRS;


public class similarityOnIndividual extends SPARQLonSimilarity{
	    
    
    public similarityOnIndividual(String similarityPrefix){
    	super(similarityPrefix);
    	this.statsToAdd = new ArrayList<Statement>();
    	this.complete = ModelFactory.createOntologyModel( OntModelSpec.OWL_MEM_MICRO_RULE_INF);
    }
	
    
    
    public void getUncertainResults(SFactorRS RS){   
    	   
		List<String> uncertainties = getUncertainties();
		Iterator<String> it = uncertainties.iterator();
		
		while(it.hasNext()){
			String element = (String)it.next();
			createUncertain(ontology, element);
		}
		
		this.set_n_added(this.statsToAdd.size());
		
		System.out.println(this.statsToAdd.size() + " statements generated:");
		
		for(int i=0;i<this.statsToAdd.size();i++){
			System.out.println(this.statsToAdd.get(i).toString());
			complete.add(this.statsToAdd.get(i));
		}
		
	
	}
    
    

    	
	
	
	private List<String> getUncertainties(){
		
        
  	    System.out.println("\n\n***** UNCERTAINTIES *****\n");	
       
  	    
        String queryString =        
	          //  "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "+
	          //  "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>  "+
	            "select ?u "+
	            "where { "+
	             "?u a <"+this.similarityPrefix+"processableUncertainty>  "+
	            "} \n ";
  	    
  	  
        
        com.hp.hpl.jena.query.Query query = QueryFactory.create(queryString); 
        QueryExecution qe = QueryExecutionFactory.create(query, similarity);
        com.hp.hpl.jena.query.ResultSet results =  qe.execSelect();
     
        
        List<String> result = new ArrayList<String>();
        
        
        
        while(results.hasNext()){
        	String s = results.next().toString();
        	s = s.substring(s.indexOf("<")+1);
        	s = s.substring(0, s.indexOf(">"));
        	//System.out.println(s);
        	result.add(s);
        	
        }
        
		return result;
	}
	
	
	
	
	private void createUncertain(OntModel ontology, String element){
		
		List<String> sims = getElement(element);
		Iterator i = ontology.listStatements();
		
		
		Statement stat = null;
		Statement curr = null;
		
		Resource subject = null;
		Property predicate = null;
		RDFNode object = null;
		
		
		while(i.hasNext()){
			
			curr=(Statement) i.next();
			
			
			Iterator<String> j= sims.iterator();
			
			while(j.hasNext()){
				
				
				
				String target = j.next().toString();
				
				if((curr.toString()).contains(target)){
					
					
					String a = element.substring(element.indexOf("#")); 
					String b = target.substring(target.indexOf("#")); 
					//System.err.println(a);
					//System.out.println(b);
					String curr1 = curr.toString().replace(b,a);
					System.out.println("Generating:\n" + curr + "-->\n" + curr1 + "\n");
					
					curr1 = curr1.substring(curr1.indexOf("[")+1, curr1.indexOf("]"));
					curr1 = curr1.replaceAll("\\s+","");
				
				    StringTokenizer tokenizer = new StringTokenizer(curr1,",");
				    
				    while(tokenizer.hasMoreTokens()){
				    	
				    	subject = ResourceFactory.createResource(tokenizer.nextToken());
				    	//System.out.println(subject.toString());
				    	predicate = ResourceFactory.createProperty(tokenizer.nextToken());
				    	//System.out.println(predicate.toString());
				    	object = ResourceFactory.createResource(tokenizer.nextToken());
				    	//System.out.println(object.toString());
				    	stat = ResourceFactory.createStatement(subject, predicate, object);
				    			
				    }//while

				    this.statsToAdd.add(stat);
					
				}//if
				
				
			}//while
			
			
		}//while
		
		
	}
	
	





	
	

}
