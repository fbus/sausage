package org.sausage.model.service;

public class ISService {

    public String fullName;
    public String packageName;
    
    public Audit audit;
    
    /**
     * Cache behavior for this flow. Will be <code>null</code> if 'Cache Results' is False.
     */
    public CacheBehavior cacheBehavior;

    public ACLs acl;
}
