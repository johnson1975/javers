package org.javers.spring.integration;

import org.javers.spring.AuthorProvider;

/**
 * @author Pawel Szymczyk
 */
class DummyAuthorProvider implements AuthorProvider {
    @Override
    public String provide() {
        return "kazik";
    }
}
