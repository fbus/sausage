package org.sausage.model.service;

public class CacheBehavior {

    /**
     * TTL (in minutes) "The expiration timer begins when the server initially caches a result, and it expires when the time you
     * specify elapses. (The server does not reset the expiration timer each time it satisfies a service request with a cached result.)
     * The minimum cache expiration time is one minute."
     */
    public int expiryInMinutes;
    /**
     * Automatically refresh the cache for this service when it expires.
     */
    public boolean prefetch;
    /**
     * minimum number of hits needed to activate the use of prefetch
     */
    public int prefetchActivation;

}