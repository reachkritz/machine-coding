public class Solution { 
    private static int max(int[] arr, int start, int end) {
        int result = 0;
        for (int i=start; i<=end; i++) {
            result = Math.max(result, arr[i]);
        }
        return result;
    }

    private static int dp(int[] arr, int i, int prevStart, int k, int[][][] minSum) {
        if (i >= arr.length) return Integer.MAX_VALUE;
        if (k == 1) return max(arr, prevStart, arr.length - 1);
        if ((arr.length - i) < k) return Integer.MAX_VALUE;
        if (minSum[prevStart][i][k] != 0) return minSum[prevStart][i][k];

        int sum = Integer.MAX_VALUE;
        int case1 = dp(arr, i+1, prevStart, k, minSum);
        int case2 = dp(arr, i+1, i, k-1, minSum);
        if (case1 != Integer.MAX_VALUE) {
            sum = Math.min(sum, case1);
        }
        if (case2 != Integer.MAX_VALUE) {
            sum = Math.min(sum, max(arr, prevStart, i-1) + case2);
        }

        minSum[prevStart][i][k] = sum;
        return minSum[prevStart][i][k];
    }

    public static void main(String[] args) {
        int[] arr = new int[]{150, 200, 400, 350, 250};
        int k = 3;
        int[][][] minSum = new int[arr.length][arr.length][k+1];
        System.out.println("Result = "+dp(arr, 1, 0, k, minSum));
    }
}
