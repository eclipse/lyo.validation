/*******************************************************************************
 * Copyright (c) 2017 Yash Khatri.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Yash Khatri - initial API and implementation and/or initial documentation
 *******************************************************************************/


/**
 *
 * @author Yash Khatri
 * @version $version-stub$
 * @since 2.3.0
 */

package org.eclipse.lyo.validation;

import java.math.BigInteger;
import java.net.URI;
import java.util.Date;

import org.apache.jena.rdf.model.Model;
import org.eclipse.lyo.oslc4j.provider.jena.JenaModelHelper;
import org.eclipse.lyo.validation.impl.ValidatorImpl;
import org.eclipse.lyo.validation.model.ResourceModel;
import org.eclipse.lyo.validation.model.ValidationResultModel;
import org.eclipse.lyo.validation.shacl.ShaclShape;
import org.eclipse.lyo.validation.shacl.ShaclShapeFactory;
import org.junit.Assert;
import org.junit.Test;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * The Class ShaclMaxExclusiveValidationTest.
 */
public class ShaclMaxExclusiveValidationTest {

	/** The a resource. */
	AResource aResource;

	/**
	 * Shacl max exclusive negativetest.
	 *
	 * This test is failing because maximum allowed value for integerproperty2 is 14.
	 */
	@Test
	public void ShaclMaxExclusiveNegativetest() {

		try {
			aResource =  new AResource(new URI("http://www.sampledomain.org/sam#AResource"));
			aResource.setAStringProperty("Between");
			aResource.setAnotherIntegerProperty(new BigInteger("12"));
			//Invalid Value. Maximum allowed value is 14.
			aResource.setIntegerProperty2(new BigInteger("16"));
			aResource.addASetOfDates(new Date());

			Model dataModel =  JenaModelHelper.createJenaModel(new Object[] {aResource});
			ShaclShape shaclShape = ShaclShapeFactory.createShaclShape(AResource.class);
			Model shapeModel =  JenaModelHelper.createJenaModel(new Object[] {shaclShape});

			Validator validator =  new ValidatorImpl();
			ValidationResultModel vr = validator.validate(dataModel, shapeModel);
			Assert.assertEquals(1, vr.getInvalidResources().size());
			Assert.assertEquals(0, vr.getValidResources().size());

			for(ResourceModel rm : vr.getInvalidResources()) {

				JsonElement jelement = new JsonParser().parse(rm.getResult().toJsonString2spaces());
				JsonObject  obj = jelement.getAsJsonObject();
				String actualError =  obj.getAsJsonArray("errors").get(0).getAsJsonObject().get("error").toString().replaceAll("\"", "").split(" ")[0];

				Assert.assertFalse(rm.getResult().isValid());
				String expectedError = "sh:maxExclusiveError";
				Assert.assertEquals(expectedError, actualError );
				Assert.assertEquals(1, rm.getResult().errors().size());
			}
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("Exception should not be thrown");
		}


	}

	/**
	 * Shacl max exclusive positivetest.
	 */
	@Test
	public void ShaclMaxExclusivePositivetest() {

		try {
			aResource =  new AResource(new URI("http://www.sampledomain.org/sam#AResource"));
			aResource.setAnotherIntegerProperty(new BigInteger("12"));
			aResource.setIntegerProperty2(new BigInteger("14"));
			aResource.setAStringProperty("Between");
			aResource.addASetOfDates(new Date());

			Model dataModel =  JenaModelHelper.createJenaModel(new Object[] {aResource});
			ShaclShape shaclShape = ShaclShapeFactory.createShaclShape(AResource.class);
			Model shapeModel =  JenaModelHelper.createJenaModel(new Object[] {shaclShape});

			Validator validator =  new ValidatorImpl();
			ValidationResultModel vr = validator.validate(dataModel, shapeModel);
			Assert.assertEquals(1, vr.getValidResources().size());
			Assert.assertEquals(0, vr.getInvalidResources().size());

			for(ResourceModel rm : vr.getValidResources()) {

				Assert.assertTrue(rm.getResult().isValid());
				Assert.assertEquals(0, rm.getResult().errors().size());
			}

		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("Exception should not be thrown");
		}

	}

}
