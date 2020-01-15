package com.claro;

import static org.junit.Assert.assertEquals;

import java.text.ParseException;

import org.junit.Test;

import com.claro.util.UtilsClaro;

public class UnitTestUtils {
	
	@Test
	public void formatDate() throws ParseException {
		String date = UtilsClaro.reverseFormat("10-01-2020 09:50:54");
		assertEquals("2020-01-10 09:50:54", date);
	}

}
