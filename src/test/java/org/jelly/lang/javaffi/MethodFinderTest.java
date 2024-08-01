package org.jelly.lang.javaffi;

import org.jelly.lang.data.Cons;
import org.jelly.lang.data.ConsList;
import org.jelly.lang.data.Nil;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import org.jelly.utils.ConsUtils;

public class MethodFinderTest {
    // da testare
    // boxed, primitive, object, e varie combinazioni di questi 3 tipi di classe

    @Test
    public void testFindMethodWithPrimitives() {
        try {
            Method m1 = MethodFinder.findMethod(LabRat.class, "methodTakingInt", (new Class<?>[] {Integer.class}));
            Method m2 = MethodFinder.findMethod(LabRat.class, "methodTakingInt", (new Class<?>[] {int.class}));
            assertEquals(m1, m2);
        } catch(NoSuchMethodException e) {
            throw new AssertionError("cannot find method, assertion failed", e);
        }
    }

    @Test
    public void testFindMethodWithBoxedType() {
        try {
            Method m1 = MethodFinder.findMethod(LabRat.class, "methodTakingBoxedInteger", (new Class<?>[] {Integer.class}));
            Method m2 = MethodFinder.findMethod(LabRat.class, "methodTakingBoxedInteger", (new Class<?>[] {int.class}));
            assertEquals(m1, m2);
        } catch(NoSuchMethodException e) {
            throw new AssertionError("cannot find method, assertion failed", e);
        }
    }

    @Test
    public void testFindMethodWithPrimitiveAndObject() {
        try {
            Method m1 = MethodFinder.findMethod(LabRat.class, "methodTakingIntAndString", (new Class<?>[] {Integer.class, String.class}));
            Method m2 = MethodFinder.findMethod(LabRat.class, "methodTakingIntAndString", (new Class<?>[] {int.class, String.class}));
            assertEquals(m1, m2);
        } catch(NoSuchMethodException e) {
            throw new AssertionError("cannot find method, assertion failed", e);
        }
    }

    @Test
    public void testFindMethodWithBoxedTypeAndObject() {
        try {
            Method m1 = MethodFinder.findMethod(LabRat.class, "methodTakingBoxedIntegerAndString", (new Class<?>[] {Integer.class, String.class}));
            Method m2 = MethodFinder.findMethod(LabRat.class, "methodTakingBoxedIntegerAndString", (new Class<?>[] {int.class, String.class}));
            assertEquals(m1, m2);
        } catch(NoSuchMethodException e) {
            throw new AssertionError("cannot find method, assertion failed", e);
        }
    }

    @Test
    public void testFindsMethodGivenInterface() {
        try {
            Method m1 = MethodFinder.findMethod(ConsUtils.class, "toList", (new Class<?>[]{Cons.class}));
            Method m2 = MethodFinder.findMethod(ConsUtils.class, "toList", (new Class<?>[]{Nil.class}));
            Method m3 = MethodFinder.findMethod(ConsUtils.class, "toList", (new Class<?>[]{ConsList.class}));
            assertEquals(m1, m2);
            assertEquals(m1, m3);
        } catch(NoSuchMethodException e) {
            throw new AssertionError("cannot find method, assertion failed", e);
        }
    }

    @Test
    public void testFindsConstructorWithPrimitives() {
        try {
            Constructor<?> c1 = MethodFinder.findConstructor(LabRat.class, (new Class<?>[]{int.class, int.class}));
            Constructor<?> c2 = MethodFinder.findConstructor(LabRat.class, (new Class<?>[]{int.class, Integer.class}));
            Constructor<?> c3 = MethodFinder.findConstructor(LabRat.class, (new Class<?>[]{Integer.class, int.class}));
            Constructor<?> c4 = MethodFinder.findConstructor(LabRat.class, (new Class<?>[]{Integer.class, Integer.class}));
            assertEquals(c1, c2);
            assertEquals(c1, c3);
            assertEquals(c1, c4);
        } catch(NoSuchMethodException e) {
            throw new AssertionError("cannot find constructor, assertion failed", e);
        }
    }

    @Test
    public void testFindsConstructorWithObject() {
        try {
            Constructor<?> c = MethodFinder.findConstructor(LabRat.class, (new Class<?>[]{String.class}));
        } catch(NoSuchMethodException e) {
            throw new AssertionError("cannot find constructor, assertion failed", e);
        }
    }
}
