
package controller;
@FunctionalInterface

interface ComplexFunction<FirstArg ,SecondArg> {
    public FirstArg apply(FirstArg x, SecondArg y);
}