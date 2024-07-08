package org.jelly.examples;

import org.jelly.eval.runtime.JellyRuntime;
import org.jelly.lang.data.LispList;
import org.jelly.utils.LispLists;

import java.util.List;
import java.util.ArrayList;

public class MatrixTransposition {
    static boolean validateMatrix(List<List<Integer>> matrix) {
        int rowLength = matrix.getFirst().size();
        for(int i = 1; i< matrix.size(); i++) {
            if(matrix.get(i).size() != rowLength) {
                return false;
            }
        }
        return true;
    }

    static List<List<Integer>> transpose(List<List<Integer>> matrix) {
        int rows = matrix.size();
        int cols = matrix.getFirst().size();

        List<List<Integer>> tr = new ArrayList<>(cols);

        for(int i = 0; i<cols; ++i) {
            tr.add(new ArrayList<>(rows));
            for(int j = 0; j<rows; ++j) {
                tr.get(i).add(matrix.get(j).get(i));
            }
        }
        return tr;
    }

    static void printMatrix(List<List<Integer>> matrix) {
        for(List<Integer> row : matrix) {
            for(Integer i : row) {
                System.out.print("" + i + " ");
            }
            System.out.println();
        }
    }

    static List<List<Integer>> transposeLisp(List<List<Integer>> matrix) {
        JellyRuntime jr = new JellyRuntime();
        jr.evalString("(define (trasposta llst)" +
                         "  (define (valid?) (allEqual? length llst))" +
                         "  (define (column i) (map (lambda (lst) (nth lst i)) llst))" +
                         "  (if (valid?)" +
                         "      (map column (range 0 (length (car llst))))" +
                         "      (error \"invalid input matrix, cannot transpose : TRASPOSTA\")))");

        LispList ll = LispLists.javaListToCons(matrix.stream().map(LispLists::javaListToCons).toList());
        LispList trl = (LispList)jr.call("trasposta", ll);

        return LispLists.lispListToJava(trl)
                .stream()
                .map(a -> LispLists.lispListToJava((LispList)a).stream().map(b->(Integer)b).toList())
                .toList();
    }


    public static void main(String[] args) {
        List<List<Integer>> lst = List.of(List.of(1, 2, 3, 4), List.of(2, 3, 4, 5), List.of(3, 4, 5, 6));
        printMatrix(lst);
        System.out.println();
        if(validateMatrix(lst)) {
            printMatrix(transposeLisp(lst));
        }
        else {
            System.out.println("error, not a valid matrix, cannot transpose");
        }
    }
}
