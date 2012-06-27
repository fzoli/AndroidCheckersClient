package org.dyndns.fzoli.mvc.test.client.model;

import org.dyndns.fzoli.mvc.client.connection.Connection;
import org.dyndns.fzoli.mvc.test.common.key.ModelKeys;

public class MyTestModel extends BaseTestModel {

	public MyTestModel(Connection<Object, Object> connection) {
		super(connection, ModelKeys.MY_TEST_MODEL);
	}

}