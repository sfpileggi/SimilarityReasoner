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

import data.SFactorRS;
import data.Uncertainty;


public class similarityOnProperty extends SPARQLonSimilarity{
	
	
	public similarityOnProperty(String similarityPrefix){
    	super(similarityPrefix);
    	this.statsToAdd = new ArrayList<Statement>();
    	this.complete = ModelFactory.createOntologyModel( OntModelSpec.OWL_MEM_MICRO_RULE_INF);
    }
	
	

	@Override
	void getUncertainResults(SFactorRS RS) {
		
		List<Uncertainty> uncertainties = getUncertainties();
		Iterator<Uncertainty> it = uncertainties.iterator();
		
		while(it.hasNext()){
			Uncertainty element = (Uncertainty)it.next();
			//element.print();
			createUncertain(ontology, element);
		}
		
		this.set_n_added(this.statsToAdd.size());
		
		System.out.println(this.statsToAdd.size() + " statements generated:");
		
		for(int i=0;i<this.statsToAdd.size();i++){
			System.out.println(this.statsToAdd.get(i).toString());
			//System.err.println(this.statsToAdd.get(i));
			complete.add(this.statsToAdd.get(i));
		}
		
	}
	
	
	private List<Uncertainty> getUncertainties(){
		
		List<Uncertainty> uncertaintyList = new ArrayList<Uncertainty>();
		
		System.out.println("\n\n***** UNCERTAINTIES *****\n");	
		
		 String queryString_onIndividual =        
		            "select ?u "+
		            "where { "+
		             "?u a <"+this.similarityPrefix+"processableUncertainty>. "+  
		            "} \n ";
	  	   	  	  
	        
	     com.hp.hpl.jena.query.Query query = QueryFactory.create(queryString_onIndividual); 
	     QueryExecution qe = QueryExecutionFactory.create(query, similarity);
	     com.hp.hpl.jena.query.ResultSet results =  qe.execSelect();
		
	     while(results.hasNext()){
	    	 
	    	 Uncertainty currSim = new Uncertainty();
	    	
	    	 	String curr =  results.next().toString();
	    	 	curr = curr.substring(curr.indexOf("<"), curr.indexOf(">")+1);
	    	 	System.out.println(curr);
	    	
	    	 	String queryString_checkProperty =        
		            "select ?p "+
		            "where { "+ 
		             curr+ " <"+this.similarityPrefix+"onProperty> ?p." +
		            "} \n ";
	    	 	
	    	 	//System.err.println("curr: "+curr + "   "+queryString_checkProperty);
	    	 	 
	    	 	com.hp.hpl.jena.query.Query query1 = QueryFactory.create(queryString_checkProperty); 
	    	 	QueryExecution qe1 = QueryExecutionFactory.create(query1, similarity);
	    	 	com.hp.hpl.jena.query.ResultSet results1 =  qe1.execSelect();
	    	 	
	    	 	boolean onProperty = false;
		     
	    	 	while(results1.hasNext()){
	    	 		
	    	 		onProperty = true;
	    	 		
	    	 		String currProperty =  results1.next().toString();
		    	 	currProperty = currProperty.substring(currProperty.indexOf("\"")+1);
		    	 	currProperty = currProperty.substring(0,currProperty.indexOf("\""));
		    	 	
		    	 	currSim = new Uncertainty();
		    	 	currSim.setSubject(curr);
		    	 	
		    	 	currSim.setProperty(currProperty);
		    	 	
		    	 	uncertaintyList.add(currSim);
		    	 	
		    	 	System.out.println("On property: "+currProperty);
	    	 	}
	    	 	
	    	 	if(!onProperty){
	    	 		currSim = new Uncertainty();
		    	 	currSim.setSubject(curr);
		    	 	uncertaintyList.add(currSim);
	    	 	}
	    	 	
	    	 	
	    }
	       
	    System.out.println("\n");
		return uncertaintyList;
	}
	
	
	private void createUncertain(OntModel ontology, Uncertainty element){
		
		String currProperty = element.getProperty();
		
		//System.err.println(currProperty);
		
		String subjectS = element.getSubject();
		subjectS = subjectS.substring(1, subjectS.length()-1);
		//System.err.println("check: "+subjectS);
		List<String> sims = getElement(subjectS);
		
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
						//System.err.println("check: "+target);
				
						if((curr.toString()).contains(target) && (curr.toString().contains(currProperty)) && !currProperty.equals("-1")){ //Similarity on property
					
							String a = element.getSubject().substring(element.getSubject().indexOf("#"),element.getSubject().indexOf(">")); 
							String b = target.substring(target.indexOf("#")); 
					
							String curr1 = curr.toString().replace(b,a);
							System.out.println("Generating:\n" + curr + "-->\n" + curr1 + "\n");
					
							curr1 = curr1.substring(curr1.indexOf("[")+1, curr1.indexOf("]"));
							curr1 = curr1.replaceAll("\\s+","");
				
							StringTokenizer tokenizer = new StringTokenizer(curr1,",");
				    
							while(tokenizer.hasMoreTokens()){
				    	
							    	subject = ResourceFactory.createResource(tokenizer.nextToken());
							    	//System.err.println(subject.toString());
							    	predicate = ResourceFactory.createProperty(tokenizer.nextToken());
							    	//System.out.println(predicate.toString());
							    	object = ResourceFactory.createResource(tokenizer.nextToken());
							    	//System.err.println(object.toString());
							    	stat = ResourceFactory.createStatement(subject, predicate, object);
				    			
							}//while

							this.statsToAdd.add(stat);
							//System.err.println(stat.getSubject());
					
						}//if
						
						if((curr.toString()).contains(target) && currProperty.equals("-1")){    //Similarities on individual
							
							String a = element.getSubject().substring(element.getSubject().indexOf("#"), element.getSubject().indexOf(">")); 
							String b = target.substring(target.indexOf("#")); 
					
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
