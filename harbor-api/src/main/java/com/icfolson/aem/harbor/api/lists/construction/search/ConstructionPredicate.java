package com.icfolson.aem.harbor.api.lists.construction.search;

import com.day.cq.search.Predicate;
import com.google.common.base.Optional;

public interface ConstructionPredicate {

    Optional<Predicate> asPredicate();

}
