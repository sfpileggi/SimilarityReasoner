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

import com.hp.hpl.jena.ontology.OntModel;


import data.SFactorRS;

public interface similarityInterface {
	
	SFactorRS QueryOnSimilarity(String ontologyFile, String similarityFile, String SPARQLQuery); // Execute a SPARQL Query from files
	SFactorRS QueryOnSimilarity(OntModel ontology, OntModel similarity, String SPARQLQuery); // Execute a SPARQL Query from models
	
}
