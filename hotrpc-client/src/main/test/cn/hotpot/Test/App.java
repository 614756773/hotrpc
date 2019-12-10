package cn.hotpot.Test;


public class App{
    public static void main(String[] args) {
        Student jam = new Student("955", "Jam", 61.5f);
        System.out.println(String.format("%s成绩%s", jam.getName(), jam.isPass() ? "及格" : "不及格"));
    }

    public static class Student {
        private String no;
        private String name;
        private float score;

        public String getNo() {
            return no;
        }

        public String getName() {
            return name;
        }

        public float getScore() {
            return score;
        }

        public Student(String no, String name, float score) {
            this.no = no;
            this.name = name;
            this.score = score;
        }

        public boolean isPass() {
            return this.score >= 60;
        }
    }
}
