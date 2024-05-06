package cn.chuanwise.xiaoming.util;

import cn.chuanwise.util.Collections;
import cn.chuanwise.util.Preconditions;
import cn.chuanwise.xiaoming.contact.message.Message;
import cn.chuanwise.xiaoming.user.XiaoMingUser;
import lombok.Data;

import java.util.*;

@Data
public class ListViewer<T> {
    private final List<T> list;

    private final int elementCountPerPage;

    private final String pageHead, pageTail;

    private final String listHead, listTail;

//    public ListViewer(List<T> list, int elementCountPerPage, String pageHead, String pageTail, String listHead, String listTail) {
//        this.list = list;
//        this.elementCountPerPage = elementCountPerPage;
//        this.pageHead = pageHead;
//        this.pageTail = pageTail;
//        this.listHead = listHead;
//        this.listTail = listTail;
//    }

    public static <U> Builder<U> builder(Collection<U> collection, int elementCountPerPage) {
        Preconditions.namedArgumentNonNull(collection, "collection");
        Preconditions.argument(elementCountPerPage > 0, "element count per page must be bigger than 0!");

        return new Builder<>(collection, elementCountPerPage);
    }

    public View<T> view(XiaoMingUser user) {
        Preconditions.namedArgumentNonNull(user, "xiao ming user");

        return new View<T>(user);
    }

    public AsyncView<T> asyncView(XiaoMingUser user) {
        Preconditions.namedArgumentNonNull(user, "xiao ming user");

        return new AsyncView(user);
    }

    public static class Builder<T> {
        private final List<T> list;

        private final int elementCountPerPage;

        private String pageHead, pageTail;

        private String listHead, listTail;

        public Builder(Collection<T> collection, int elementCountPerPage) {
            Preconditions.namedArgumentNonNull(collection, "collections");
            Preconditions.argument(elementCountPerPage > 0, "element count per page must be bigger than 0!");

            if (collection instanceof List)
                this.list = (List<T>) collection;
            else
                this.list = Collections.asList(collection);

            this.elementCountPerPage = elementCountPerPage;
        }

        public Builder<T> pageHead(String pageHead) {
            this.pageHead = pageHead;

            return this;
        }

        public Builder<T> pageTail(String pageTail) {
            this.pageTail = pageTail;

            return this;
        }

        public Builder<T> listHead(String listHead) {
            this.listHead = listHead;

            return this;
        }

        public Builder<T> listTail(String listTail) {
            this.listTail = listTail;

            return this;
        }

        public ListViewer<T> build() {
            return new ListViewer<>(list, elementCountPerPage, pageHead, pageTail, listHead, listTail);
        }
    }

    @Data
    public class View<T> {
        private final XiaoMingUser user;
        List<String> pages = new ArrayList<>(elementCountPerPage / list.size() + 1);
        private int currentPage;
        private volatile boolean showing;

        public View(XiaoMingUser user) {
            Preconditions.namedArgumentNonNull(user, "xiao ming user");

            this.user = user;
        }

        public ListViewer<T> listView() {
            return (ListViewer<T>) ListViewer.this;
        }

        protected List<String> getPages() {
            List<StringBuilder> tempPages = new ArrayList<>();

            int currentIndex = 0;
            StringBuilder sb = new StringBuilder();

            if (!Objects.isNull(listHead))
                sb.append(listHead).append("\n");
            if (!Objects.isNull(pageHead))
                sb.append(pageHead).append("\n");

            for (Object o : list) {
                if (currentIndex < elementCountPerPage) {
                    sb.append(++currentIndex).append("、").append(o).append("\n");
                } else {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ignored) {
                    }

                    if (!Objects.isNull(pageTail))
                        sb.append(pageTail);
                    tempPages.add(sb);

                    currentIndex = 0;
                    sb = new StringBuilder();
                    if (!Objects.isNull(pageHead))
                        sb = new StringBuilder().append(pageHead).append("\n");

                    sb.append(++currentIndex).append("、").append(o).append('\n');
                }
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException ignored) {
            }

            if (!Objects.isNull(pageTail))
                sb.append(pageTail).append("\n");
            if (!Objects.isNull(listTail))
                sb.append(listTail);
            tempPages.add(sb);

            for (int i = 0; i < tempPages.size(); i++) {
                StringBuilder stringBuilder = tempPages.get(i);
                if (i == 0)
                    stringBuilder.insert(stringBuilder.toString().indexOf('\n') + 1,
                            "第 " + (++currentPage) + " 页，共 " + tempPages.size() + " 页\n");
                else
                    stringBuilder.insert(0,
                            "第 " + (++currentPage) + " 页，共 " + tempPages.size() + " 页\n");

                pages.add(stringBuilder.toString());
            }

            return pages;
        }

        public void show() throws InterruptedException {
            Preconditions.state(!showing, "A list is showing now!");
            try {
                showing = true;

                getPages().forEach(user::sendMessage);
            } finally {
                showing = false;
            }
        }
    }

    public class AsyncView<T> extends View<T> {
        private int currentPage;

//        private final Object mutex = new Object();

        public AsyncView(XiaoMingUser user) {
            super(user);
        }

        @Override
        public void show() throws InterruptedException, NullPointerException {
            Preconditions.state(!isShowing(), "A list is showing now!");

            try {
                setShowing(true);
                getPages();
                getUser().sendMessage(MiraiCodes.contentToString(pages.get(currentPage)));

                label:
                while (true) {
                    Optional<Message> optional = (Optional<Message>) getUser().nextMessage();
                    Message message = null;
                    if (optional.isPresent())
                        message = optional.get();
                    switch (Objects.requireNonNull(message).serialize()) {
                        case "previous":
                        case "prev":
                        case "pre":
                        case "上一页":
                        case "上":
                        case "-":
                            if (prevPage())
                                getUser().sendMessage(MiraiCodes.contentToString(pages.get(--currentPage)));
                            else
                                getUser().sendMessage("已经是第一页了哦");
                            break;
                        case "next":
                        case "下一页":
                        case "下":
                        case "+":
                            if (nextPage())
                                getUser().sendMessage(MiraiCodes.asMessageChain(pages.get(++currentPage)));
                            else
                                getUser().sendMessage("已经是最后一页了哦");
                            break;
                        case "quit":
                        case "退出":
                            getUser().sendMessage("退出成功");
                            break label;
                        default:
                            getUser().sendMessage("你应该告诉我「上一页」「下一页」或者「退出」哦");
                            break;
                    }

                }
            } finally {
                setShowing(false);

//                synchronized (mutex) {
//                    mutex.notifyAll();
//                }
            }
        }

        /**
         * 前往下一页
         *
         * @return 是否成功前往下一页
         */
        public boolean nextPage() {
            return currentPage < list.size() / elementCountPerPage - 1;
        }

        /**
         * 返回上一页
         *
         * @return 是否成功返回上一页
         */
        public boolean prevPage() {
            return currentPage > 0;
        }

//        public void sync() throws InterruptedException {
//            if (!isShowing()) {
//                return;
//            }
//
//            synchronized (mutex) {
//                mutex.wait();
//            }
//        }
//
//        public void await(long duration, TimeUnit timeUnit) throws InterruptedException {
//            Preconditions.argument(duration > 0);
//            Preconditions.namedArgumentNonNull(timeUnit, "time unit");
//
//            if (!isShowing()) {
//                return;
//            }
//
//            synchronized (mutex) {
//                mutex.wait(timeUnit.toMillis(duration));
//            }
//        }
//
//        public void await(long timeout) throws InterruptedException {
//            Preconditions.argument(timeout > 0);
//
//            await(timeout, TimeUnit.MILLISECONDS);
//        }
//
//        public void syncUninterruptedly() {
//            while (isShowing()) {
//                try {
//                    sync();
//                } catch (InterruptedException ignored) {
//                }
//            }
//        }
    }
}
