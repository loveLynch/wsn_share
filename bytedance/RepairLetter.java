package bytedance;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/*
 *@author Lynch
 *@date 2019/7/31 9:04
 */
public class RepairLetter {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        int N = Integer.parseInt(input.nextLine());
        for (int i = 0; i < N; i++) {
            String line = input.nextLine();
            System.out.println(getNoRepairLetter(line));
        }
    }

    private static String getNoRepairLetter(String line) {
        List<Character> letterList = new ArrayList<>();
        for (char c : line.toCharArray())
            letterList.add(c);
        int letterLength = letterList.size();

        //去掉三个连载一起的中的一个
        int i = 0;
        while (i < letterLength - 2) {
            if (letterList.get(i) == letterList.get(i + 1) && letterList.get(i + 1) == letterList.get(i + 2)) {
                letterList.remove(i + 2);
                letterLength--;
                i--;
            }
            i++;
        }
        //去掉AABB中的一个B
        int j = 0;
        while (j < letterLength - 3) {
            if (letterList.get(j) == letterList.get(j + 1) && letterList.get(j + 2) == letterList.get(j + 3)) {
                letterList.remove(j + 3);
                letterLength--;
                j--;
            }
            j++;
        }

        StringBuilder result = new StringBuilder();
        for (char c : letterList)
            result.append(c);
        return result.toString();
    }
}
