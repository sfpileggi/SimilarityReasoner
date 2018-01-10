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

public class Uncertainty {
	
	private String subject;
	private String property;

	
	public Uncertainty(){
		this.subject = "-1";
		this.property = "-1";
	}
	
	
	public void setSubject(String subject){
		this.subject = subject;
	}
	
	public String getSubject(){
		return this.subject;
	}
	
	
	
	public void setProperty(String property){
		this.property = property;
	}
	
	public String getProperty(){
		return this.property;
	}
	
	
	public void print(){
		
		if(this.property.equals("-1")){
			System.err.println("Uncertainty: "+this.getSubject()+"\n");
		}
		else {
			System.err.println("Uncertainty: "+this.getSubject()+"\nOn Property: "+ this.getProperty() + "\n");
		}
	}
	
	
}
