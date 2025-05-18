public class Character {
    private static int func(byte[] arr){
        int mask = 1<<7;
        if ((arr[arr.length - 1] & mask)>>7 == 0) return arr.length - 1;
        if (arr.length>=2 && (arr[arr.length - 2] & mask)>>7 == 1) return arr.length - 2;
        return -1;
    }
    
    public static void main(String[] args) {
        byte[] input = new byte[]{100, -128, -128};
        System.out.println("Result = "+ func(input));
    }
}
