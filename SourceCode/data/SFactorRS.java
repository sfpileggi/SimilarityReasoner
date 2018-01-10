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

package data;

import java.io.ByteArrayOutputStream;

import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSetFormatter;

public class SFactorRS extends FullRS{
	
	private float similarityFactor;
	
	public SFactorRS(){
		super();
		this.similarityFactor = -1;
	}
	
	public float getSimilarityFactor(){
		
		return this.similarityFactor;
	
	}
	
	public void setSimilaityFactor(float similarityFactor){
		
		this.similarityFactor = similarityFactor;
	}
	
/*************** Visualization *************/
	
	public void printDeterministic(String SPARQLQuery){
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		com.hp.hpl.jena.query.Query query = QueryFactory.create(SPARQLQuery); 
        ResultSetFormatter.out(baos, this.deterministic, query);
        String resultString=baos.toString();
        System.out.println("\n***** Deteministic Output *****\n"+resultString);
        
	}
	
	public void printUncertain(String SPARQLQuery){
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		com.hp.hpl.jena.query.Query query = QueryFactory.create(SPARQLQuery); 
        ResultSetFormatter.out(baos, this.uncertain, query);
        String resultString=baos.toString();
        System.out.println("\n***** Output including Uncertainties *****\n SimilarityFactor: "+ this.getSimilarityFactor() + "\n" + resultString);
	}
	
}
