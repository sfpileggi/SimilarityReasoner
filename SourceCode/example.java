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




import data.SFactorRS;
import reasoner.similarityOnIndividual;
import reasoner.similarityOnProperty;

public class example {
	
	// The CONTEXT ontology + The SIMILARITY ontology
		private String	ontology	= "file:///Users/flavio/Desktop/UC1.owl";
		private String	similarity	= "file:///Users/flavio/Desktop/similarity.owl";
		
		private String similarityPrefix = "http://www.flaviopileggi.net/Ontology/similarity#";
		
	//query	
		String queryString =        
	            "select ?surfer "+
	            "where { "+
	             "?surfer a <http://www.semanticweb.org/flavio/2016/4/UC1#Surfer>  "+
	            "} \n ";
				
		
		
		public void run() {

		        SFactorRS rs = new SFactorRS();
		        SFactorRS rs1 = new SFactorRS();
		        
		        //The class similarityOnIndividual considers ONLY full similarities (similarity-on-individual)
		        rs	=	new similarityOnIndividual(similarityPrefix).QueryOnSimilarity(ontology, similarity, queryString);
			    rs.printDeterministic(queryString);
			    rs.printUncertain(queryString);
			    
			    /*The class similarityOnProperty processes both full similarities (similarity-on-individual)
			     and partial similarities (similarity-on-property)
			     */
			    rs1 = new similarityOnProperty(similarityPrefix).QueryOnSimilarity(ontology, similarity, queryString);
			    rs1.printDeterministic(queryString);
			    rs1.printUncertain(queryString);
			    
		}
	
		
		

	public static void main(String[] args) {
		example app = new example();
		app.run();

	}
	
	

}
