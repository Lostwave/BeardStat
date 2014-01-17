package com.tehbeard.beardstat.containers.documents;

import com.tehbeard.beardstat.containers.EntityStatBlob;

/**
 *
 * @author James
 */
public interface IStatDynamicDocument extends IStatDocument {
	public void updateDocument(EntityStatBlob blob);
}
