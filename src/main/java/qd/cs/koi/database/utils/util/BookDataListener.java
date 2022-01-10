package qd.cs.koi.database.utils.util;


import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qd.cs.koi.database.dao.BookDao;
import qd.cs.koi.database.dao.UserDao;
import qd.cs.koi.database.entity.BookDO;
import qd.cs.koi.database.entity.UserDO;
import qd.cs.koi.database.interfaces.Book.BookExcelDTO;
import qd.cs.koi.database.interfaces.User.UserExcelDTO;
import qd.cs.koi.database.utils.Enums.PermissionEnum;
import qd.cs.koi.database.utils.web.ApiExceptionEnum;
import qd.cs.koi.database.utils.web.AssertUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

// 有个很重要的点 UserDataListener 不能被spring管理，要每次读取excel都要new,然后里面用到spring可以构造方法传进去
public class BookDataListener extends AnalysisEventListener<BookExcelDTO> {
        private static final Logger LOGGER = LoggerFactory.getLogger(BookDataListener.class);
        //这里写持久层的类
        private BookDao bookDao;

        /**
         * 如果使用了spring,请使用这个构造方法。每次创建Listener的时候需要把spring管理的类传进来
         */

        public BookDataListener(BookDao bookDao) {
            this.bookDao = bookDao;
        }


        /**
         * 每隔5条存储数据库，实际使用中可以3000条，然后清理list ，方便内存回收
         */
        private static final int BATCH_COUNT = 5;
        List<BookExcelDTO> list = new ArrayList<BookExcelDTO>();
        /**
         * 假设这个是一个DAO，当然有业务逻辑这个也可以是一个service。当然如果不用存储这个对象没用。
         */
        //private DemoDAO demoDAO;
        public BookDataListener() {
            // 这里是demo，所以随便new一个。实际使用如果到了spring,请使用下面的有参构造函数
            //demoDAO = new DemoDAO();
        }
        /**
         * 如果使用了spring,请使用这个构造方法。每次创建Listener的时候需要把spring管理的类传进来
         *
         * @param demoDAO
         */
       /* public UserDataListener(DemoDAO demoDAO) {
            this.demoDAO = demoDAO;
        }*/
        /**
         * 这个每一条数据解析都会来调用
         *
         * @param data
         *            one row value. Is is same as {@link AnalysisContext#readRowHolder()}
         * @param context
         */
        @Override
        public void invoke(BookExcelDTO data, AnalysisContext context) {
            LOGGER.info("解析到一条数据:{}",data.toString());
            list.add(data);
        }
        /**
         * 所有数据解析完成了 都会来调用
         *
         * @param context
         */
        @Override
        public void doAfterAllAnalysed(AnalysisContext context) {
            // 这里也要保存数据，确保最后遗留的数据也存储到数据库
            saveData();
            LOGGER.info("所有数据解析完成！");
        }
        /**
         * 加上存储数据库
         */
        private void saveData() {
            LOGGER.info("{}条数据，开始存储数据库！", list.size());
            //因为我excel模板的实体和插入数据库实体的类不一样，所以需要进行转化

            List<BookDO> bookDOS = list.stream().map(o -> BookDO.builder()
                    .bookName(o.getBookname())
                    .author(o.getAuthor())
                    .bookName(o.getBookname())
                    .description(o.getDescription())
                    .classification(o.getClassification())
                    .keyWord(o.getKeyWord())
                    .price(o.getPrice())
                    .build()).collect(Collectors.toList());

            AssertUtils.isTrue(bookDao.saveBatch(bookDOS), ApiExceptionEnum.UNKNOWN_ERROR);
        }

}
