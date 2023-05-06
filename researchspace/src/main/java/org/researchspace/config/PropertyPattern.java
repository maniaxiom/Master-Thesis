/**
 * ResearchSpace
 * Copyright (C) 2020, © Trustees of the British Museum
 * Copyright (C) 2015-2019, metaphacts GmbH
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.researchspace.config;

import com.google.common.base.Throwables;

import org.eclipse.rdf4j.query.algebra.Projection;
import org.eclipse.rdf4j.query.algebra.TupleExpr;
import org.eclipse.rdf4j.query.parser.sparql.SPARQLParser;
import org.researchspace.sparql.renderer.MpSparqlQueryRenderer;
import org.researchspace.sparql.visitors.BindingNamesCollector;
import org.researchspace.sparql.visitors.VarRenameVisitor;

import java.util.Set;

/**
 * Represents SPARQL graph pattern to extract some property from a subject by
 * SPARQL property path or arbitrary pattern.
 */
public final class PropertyPattern {
    private TupleExpr propertyExpression;

    private PropertyPattern(TupleExpr propertyExpression) {
        this.propertyExpression = propertyExpression;

        BindingNamesCollector collector = new BindingNamesCollector();
        propertyExpression.visit(collector);
        Set<String> bindings = collector.getBindingNames();

        if (!bindings.contains("subject")) {
            throw new IllegalArgumentException("Property pattern missing ?subject binding");
        }
        if (!bindings.contains("value")) {
            throw new IllegalArgumentException("Property pattern missing ?value binding");
        }
    }

    private static boolean isSparqlPattern(String propertyPattern) {
        return propertyPattern.startsWith("{") && propertyPattern.endsWith("}");
    }

    public static PropertyPattern parse(String pathOrSparqlPattern, NamespaceRegistry namespaceRegistry) {
        String query;
        if (isSparqlPattern(pathOrSparqlPattern)) {
            query = "SELECT * WHERE " + pathOrSparqlPattern;
        } else {
            query = "SELECT * WHERE { ?subject " + pathOrSparqlPattern + " ?value }";
        }
        query = namespaceRegistry.prependSparqlPrefixes(query);

        SPARQLParser parser = new SPARQLParser();
        Projection projection = (Projection) parser.parseQuery(query, null).getTupleExpr();
        TupleExpr expression = projection.getArg();
        return new PropertyPattern(expression);
    }

    public String format(String subjectVarName, String propertyValueVarName) {
        TupleExpr expression = propertyExpression.clone();
        if (!subjectVarName.equals("subject")) {
            expression.visit(new VarRenameVisitor("subject", subjectVarName));
        }
        if (!propertyValueVarName.equals("value")) {
            expression.visit(new VarRenameVisitor("value", propertyValueVarName));
        }

        try {
            return new MpSparqlQueryRenderer().render(expression);
        } catch (Exception ex) {
            throw Throwables.propagate(ex);
        }
    }
}
