/*
 *  Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.ballerinalang.test.expressions.invocations;

import org.ballerinalang.test.BCompileUtil;
import org.ballerinalang.test.BRunUtil;
import org.ballerinalang.test.CompileResult;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.ballerinalang.test.BAssertUtil.validateError;

/**
 * Local function invocation test.
 *
 * @since 0.8.0
 */
public class FuncInvocationExprTest {

    private CompileResult funcInvocationExpResult;
    private CompileResult funcInvocationNegative;
    private CompileResult methodInvocationNegative;

    @BeforeClass
    public void setup() {
        funcInvocationExpResult = BCompileUtil.compile("test-src/expressions/invocations/function-invocation-expr.bal");
        funcInvocationNegative = BCompileUtil.compile("test-src/expressions/invocations/function_call_negative.bal");
        methodInvocationNegative = BCompileUtil.compile("test-src/expressions/invocations/method_call_negative.bal");
    }

    @Test
    public void invokeFunctionWithParams() {
        Object[] args = new Object[]{(1), (2)};
        Object values = BRunUtil.invoke(funcInvocationExpResult, "add", args);
        Assert.assertTrue(values instanceof Long);
        Assert.assertEquals((values), 3L);
    }

    @Test(description = "Test local function invocation expression")
    public void testFuncInvocationExpr() {
        Object[] args = {(100), (5), (1)};
        Object returns = BRunUtil.invoke(funcInvocationExpResult, "testFuncInvocation", args);

        Assert.assertSame(returns.getClass(), Long.class);

        long actual = (long) returns;
        long expected = 116;
        Assert.assertEquals(actual, expected);
    }

    @Test(description = "Test recursive function invocation")
    public void testFuncInvocationExprRecursive() {
        Object[] args = {(7)};
        Object returns = BRunUtil.invoke(funcInvocationExpResult, "sum", args);

        Assert.assertSame(returns.getClass(), Long.class);

        long actual = (long) returns;
        long expected = 28;
        Assert.assertEquals(actual, expected);

    }

    @Test(description = "Test local function invocation expression advanced")
    public void testFuncInvocationExprAdvanced() {
        Object[] args = {(100), (5), (1)};
        Object returns = BRunUtil.invoke(funcInvocationExpResult, "funcInvocationWithinFuncInvocation", args);

        Assert.assertSame(returns.getClass(), Long.class);

        long actual = (long) returns;
        long expected = 322;
        Assert.assertEquals(actual, expected);
    }

    @Test
    public void testReturnFuncInvocationWithinFuncInvocation() {
        Object[] args = {(2), (3)};
        Object returns =
                BRunUtil.invoke(funcInvocationExpResult, "testReturnFuncInvocationWithinFuncInvocation", args);

        Assert.assertSame(returns.getClass(), Long.class);

        long actual = (long) returns;
        long expected = 8;
        Assert.assertEquals(actual, expected);
    }

    @Test
    public void testReturnNativeFuncInvocationWithinNativeFuncInvocation() {
        Object[] args = {(2)};
        Object returns = BRunUtil.invoke(funcInvocationExpResult,
                "testReturnNativeFuncInvocationWithinNativeFuncInvocation", args);

        Assert.assertSame(returns.getClass(), Double.class);

        double actual = (double) returns;
        double expected = 2;
        Assert.assertEquals(actual, expected);
    }

    @Test
    public void testNativeInvocation() {
        Object[] args = {(2), (2)};
        Object returns = BRunUtil.invoke(funcInvocationExpResult, "getPowerOfN", args);
        Assert.assertSame(returns.getClass(), Double.class);
        double actual = (double) returns;
        double expected = 4;
        Assert.assertEquals(actual, expected);
    }

    @Test(dataProvider = "invocationWithArgVarargMixTestFunctions")
    public void testInvocationWithArgVarargMix(String function) {
        BRunUtil.invoke(funcInvocationExpResult, function);
    }

    @DataProvider(name = "invocationWithArgVarargMixTestFunctions")
    public Object[][] invocationWithArgVarargMixTestFunctions() {
        return new Object[][]{
                {"testInvocationWithArgVarargMixWithoutDefaultableParam"},
                {"testInvocationWithArgVarargMixWithDefaultableParam"},
                {"testVarargEvaluationCount"},
                {"testMethodInvocationWithArgVarargMixWithoutDefaultableParam"},
                {"testMethodInvocationWithArgVarargMixWithDefaultableParam"},
                {"testMethodVarargEvaluationCount"},
                {"testVarargForParamsOfDifferentKinds"},
                {"testArrayVarArg"},
                {"testTypeRefTypedRestArg"}
        };
    }

    @Test
    public void testFuncWithNeverReturnTypeWithoutVariableAssignment() {
        BRunUtil.invoke(funcInvocationExpResult, "testFuncWithNeverReturnTypeWithoutVariableAssignment");
    }

    @Test
    public void testFuncWithNilReturnTypeWithoutVariableAssignment() {
        BRunUtil.invoke(funcInvocationExpResult, "testFuncWithNilReturnTypeWithoutVariableAssignment");
    }

    @Test
    public void testFunctionCallNegativeCases() {
        int i = 0;
        validateError(funcInvocationNegative, i++, "incompatible types: expected 'int', found 'string'", 3, 16);
        validateError(funcInvocationNegative, i++, "undefined function 'foo'", 11, 5);
        validateError(funcInvocationNegative, i++, "invalid token 'private'", 14, 1);
        validateError(funcInvocationNegative, i++, "incompatible types: expected 'string', found 'int'", 22, 16);
        validateError(funcInvocationNegative, i++, "incompatible types: expected 'boolean[]', found '[int,boolean," +
                "boolean]'", 22, 26);
        validateError(funcInvocationNegative, i++, "incompatible types: expected 'boolean', found 'string'", 24, 24);
        validateError(funcInvocationNegative, i++, "incompatible types: expected 'boolean', found 'int'", 26, 27);
        validateError(funcInvocationNegative, i++, "incompatible types: expected 'boolean', found 'string'", 26, 30);
        validateError(funcInvocationNegative, i++, "variable assignment is required", 28, 5);
        validateError(funcInvocationNegative, i++,
                "incompatible types: expected '([int,boolean...]|record {| int i; |})', " +
                        "found '[float,string...]'", 28, 12);
        validateError(funcInvocationNegative, i++, "incompatible types: expected 'string', found 'int'", 31, 9);
        validateError(funcInvocationNegative, i++,
                "incompatible types: expected '([float,boolean...]|record {| float f?; |})', " +
                        "found '[int,boolean,boolean]'", 31, 15);
        validateError(funcInvocationNegative, i++,
                "incompatible types: expected '([string,float,boolean...]|record {| string s; float f?; |})', " +
                        "found '[float,string...]'", 33, 12);
        validateError(funcInvocationNegative, i++, "rest argument not allowed after named arguments", 39, 20);
        validateError(funcInvocationNegative, i++,
                "incompatible types: expected '([float,boolean...]|record {| float f?; |})', found 'boolean[]'",
                41, 23);
        validateError(funcInvocationNegative, i++, "incompatible types: expected 'boolean', found 'float'", 47, 20);
        validateError(funcInvocationNegative, i++,
                "incompatible types: expected '([int,int,int...]|record {| int i; int j; |})', found 'int[1]'",
                59, 24);
        validateError(funcInvocationNegative, i++,
                "incompatible types: expected '([int,int,int...]|record {| int i; int j; |})', found '" +
                        "(int|string)[3]'", 62, 24);
        validateError(funcInvocationNegative, i++,
                "incompatible types: expected '([int,int,string...]|record {| int i; int j; |})', found '" +
                        "(int|string)[3]'", 63, 40);
        validateError(funcInvocationNegative, i++,
                "incompatible types: expected '([int]|record {| int i; |})', found 'int[5]'", 66, 25);
        validateError(funcInvocationNegative, i++,
                "incompatible types: expected '([int,int,string...]|record {| int i; int j; |})', found " +
                        "'int[5]'", 69, 40);
        validateError(funcInvocationNegative, i++,
                "incompatible types: expected '([int,int,string...]|record {| int i; int j; |})', found " +
                        "'int[]'", 70, 40);
        validateError(funcInvocationNegative, i++,
                "incompatible types: expected 'int[]', found '(int|string)[3]'", 73, 29);
        validateError(funcInvocationNegative, i++,
                "incompatible types: expected 'int[]', found 'anydata[]'", 74, 29);
        validateError(funcInvocationNegative, i++,
                "too many arguments in call to 'sum()'", 102, 18);
        validateError(funcInvocationNegative, i++,
                "too many arguments in call to 'sum()'", 103, 18);
        validateError(funcInvocationNegative, i++,
                "incompatible types: expected 'string', found 'int[]'", 105, 33);
        validateError(funcInvocationNegative, i++,
                "missing required parameter 's' in call to 'fromString()'", 106, 16);
        validateError(funcInvocationNegative, i++,
                "undefined defaultable parameter 'ss'", 106, 31);
        validateError(funcInvocationNegative, i++, "variable assignment is required", 110, 5);
        validateError(funcInvocationNegative, i++, "variable assignment is required", 111, 5);
        validateError(funcInvocationNegative, i++, "variable assignment is required", 112, 5);
        Assert.assertEquals(i, funcInvocationNegative.getErrorCount());
    }

    @Test
    public void testMethodCallNegativeCases() {
        int i = 0;
        validateError(methodInvocationNegative, i++, "incompatible types: expected 'string', found 'int'", 23, 16);
        validateError(methodInvocationNegative, i++, "incompatible types: expected 'boolean[]', found '[int,boolean," +
                "boolean]'", 23, 28);
        validateError(methodInvocationNegative, i++, "incompatible types: expected 'boolean', found 'string'", 25, 26);
        validateError(methodInvocationNegative, i++, "incompatible types: expected 'boolean', found 'int'", 27, 29);
        validateError(methodInvocationNegative, i++, "incompatible types: expected 'boolean', found 'string'", 27, 32);
        validateError(methodInvocationNegative, i++, "variable assignment is required", 29, 5);
        validateError(methodInvocationNegative, i++,
                "incompatible types: expected '([int,boolean...]|record {| int i; |})', " +
                        "found '[float,string...]'", 29, 14);
        validateError(methodInvocationNegative, i++, "incompatible types: expected 'string', found 'int'", 32, 12);
        validateError(methodInvocationNegative, i++,
                "incompatible types: expected '([float,boolean...]|record {| float f?; |})', " +
                        "found '[int,boolean,boolean]'", 32, 18);
        validateError(methodInvocationNegative, i++,
                "incompatible types: expected '([string,float,boolean...]|record {| string s; float f?; |})', " +
                        "found '[float,string...]'", 34, 15);
        validateError(methodInvocationNegative, i++, "rest argument not allowed after named arguments", 42, 22);
        validateError(methodInvocationNegative, i++,
                "incompatible types: expected '([float,boolean...]|record {| float f?; |})', found 'boolean[]'",
                44, 26);
        validateError(methodInvocationNegative, i++, "incompatible types: expected 'boolean', found 'float'", 50, 22);
        Assert.assertEquals(i, methodInvocationNegative.getErrorCount());
    }
}
