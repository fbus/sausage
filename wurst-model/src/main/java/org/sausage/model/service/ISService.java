package org.sausage.model.service;

import org.sausage.model.AbstractAsset;

public class ISService extends AbstractAsset {
    
    public Audit audit;
    
    /**
     * Cache behavior for this flow. Will be <code>null</code> if 'Cache Results' is False.
     */
    public CacheBehavior cacheBehavior;

    public AccessControlList acl;

	public Signature signature;
}
