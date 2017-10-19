package botrev3.flow;


enum Shop{
    NullShop(""),
    Bang("banggood.com"){
        @Override
        void login(CustomTask task) {
            return ;
        }

        @Override
        void doBeforeClick(CustomTask task) {
            return ;
        }

        @Override
        void click(CustomTask task) {
            return ;
        }

        @Override
        void doAfterClick(CustomTask task) {
            return ;
        }

        @Override
        void extraFlow(CustomTask task) {
            return ;
        }
    };
    Shop(String host){
        this.host = host;
    }
    void login(CustomTask task){}
    void doBeforeClick(CustomTask task){}
    void click(CustomTask task){}
    void doAfterClick(CustomTask task){}
    void extraFlow(CustomTask task){}

    private String host;
    public String getHost() {return host;}
}
