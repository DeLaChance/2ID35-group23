/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package estimation;

import static java.lang.System.console;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author rik
 */
public class Query {

    private String query;

    Query(String query) {
        this.query = query;
    }

    public String getQueryString() {
        return this.query;
    }

    /**
     * Checks if query consists of multiple queries, e.g. //A/B/C
     *
     * @return true iff there exists a / after //
     */
    public boolean containsNextQuery() {
        Pattern pattern = Pattern.compile("//.+?/");
        Matcher matcher = pattern.matcher(query);
        return matcher.find();
    }

    /**
     * Checks if query contains a filter, e.g. //A[b]
     *
     * @return true iff there exists a [ and ] after //
     */
    public boolean containsFilter() {
        Pattern pattern = Pattern.compile("//.+?\\[.+?\\]");
        Matcher matcher = pattern.matcher(query);
        return matcher.find();
    }

    public Query getNextQuery() {
        return new Query("");
    }

    public String getStep() {
        return "";
        
    }
    
    public FilterQuery getFilter() {
        return new FilterQuery("");
    }
}
