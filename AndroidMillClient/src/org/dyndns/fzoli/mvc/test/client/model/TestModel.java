package org.dyndns.fzoli.mvc.test.client.model;

import org.dyndns.fzoli.mvc.client.connection.Connection;
import org.dyndns.fzoli.mvc.test.common.key.ModelKeys;

public class TestModel extends BaseTestModel {

	public TestModel(Connection<Object, Object> connection) {
		super(connection, ModelKeys.TEST_MODEL);
	}

}
