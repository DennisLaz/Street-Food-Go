package gr.hua.dit.noc.core;

import gr.hua.dit.noc.core.model.LookupResult;

/**
 * Person directory lookup service.
 *
 * @author Dimitris Gkoulis
 */
public interface LookupService {

    LookupResult lookupByUsername(final String username);
}
