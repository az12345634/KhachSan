package com.example.KhachSan.service.impl;

import com.itextpdf.kernel.color.Color;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.border.Border;
import com.itextpdf.layout.border.DashedBorder;
import com.itextpdf.layout.border.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.TextAlignment;
import com.example.KhachSan.entity.*;
import com.example.KhachSan.model.dto.OrderDTO;
import com.example.KhachSan.model.dto.OrderDetailDTO;
import com.example.KhachSan.model.dto.RoomDTO;
import com.example.KhachSan.model.dto.UserDTO;
import com.example.KhachSan.model.request.OrderFilterRequest;
import com.example.KhachSan.model.response.BaseResponse;
import com.example.KhachSan.repository.*;
import com.example.KhachSan.service.IOrderService;
import com.example.KhachSan.service.IUserService;
import com.example.KhachSan.utils.Constant;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements IOrderService {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private IUserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private OrderDetailRepository orderDetailRepository;
    @Autowired
    private RoomRepository roomRepository;


    @Override
    public BaseResponse<Page<OrderDTO>> getAll(OrderFilterRequest orderFilterRequest, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<OrderEntity> orderEntities = orderRepository.findAllByFilter(orderFilterRequest, pageable);

        List<OrderDTO> orderDTOS = new ArrayList<>();

        for (OrderEntity orderEntity : orderEntities.getContent()) {
            OrderDTO orderDTO = modelMapper.map(orderEntity, OrderDTO.class);
            orderDTO.setTotalAmount(orderEntity.getTotalAmount());
            orderDTO.setUsername(orderEntity.getUserEntity().getUsername());
            orderDTO.setCreateDate(orderEntity.getCreatedDate());

            // Lấy danh sách chi tiết đơn hàng
            List<OrderDetailEntity> orderDetails = orderDetailRepository.findByCode(orderEntity.getId());

            // Tạo danh sách tên sản phẩm cho mỗi đơn hàng
            List<String> productNames = new ArrayList<>();
            for (OrderDetailEntity orderDetail : orderDetails) {
                RoomEntity roomEntity = orderDetail.getRoomEntity();
                productNames.add(roomEntity.getName()); // Giả sử tên sản phẩm được lưu trong trường 'name'
            }

            // Đặt danh sách tên sản phẩm cho đơn hàng
            orderDTO.setProductname(String.join(", ", productNames));

            orderDTOS.add(orderDTO);
        }

        Page<OrderDTO> pageData = new PageImpl<>(orderDTOS, pageable, orderEntities.getTotalElements());

        BaseResponse<Page<OrderDTO>> response = new BaseResponse<>();
        response.setCode(HttpStatus.OK.value());
        response.setMessage("success");
        response.setData(pageData);

        return response;
    }

    @Override
    public BaseResponse<OrderDTO> getDetailByCode(OrderFilterRequest request) {
        String code = request.getCode();
        UserDTO userDTO = userService.getCurrentUser(true);
        // Lấy bản ghi đơn hàng theo mã đơn hàng
        Optional<OrderEntity> orderEntity = orderRepository.getByCode(userDTO.getId(), code);

        BaseResponse<OrderDTO> response = new BaseResponse<>();
        if (orderEntity.isEmpty()) {
            response.setCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage("User not exits in system");
            return response;
        }
        // map OrderEntity sang OrderDTO
        OrderDTO orderDTO = modelMapper.map(orderEntity, OrderDTO.class);

        // Lấy danh sách chi tiết các sản phẩm và size trong đơn hàng và trả về 1 list chi tiết
        List<OrderDetailDTO> orderDetailDTOS = orderDetailRepository.findByCode(orderEntity.get().getId()).stream().map(orderDetailEntity -> {
            OrderDetailDTO orderDetailDTO = new OrderDetailDTO();

            orderDetailDTO.setTotal(orderDetailEntity.getTotal());
            orderDetailDTO.setQuantity(orderDetailEntity.getQuantity());

            Optional<RoomEntity> product = roomRepository.findById(orderDetailEntity.getRoomEntity().getId());
            if (product.isEmpty()) {
                orderDetailDTO.setProductName(null);
                orderDetailDTO.setProductCode(null);
            } else {
                orderDetailDTO.setProductName(product.get().getName());
                orderDetailDTO.setProductCode(product.get().getCode());
            }

            return orderDetailDTO;
        }).collect(Collectors.toList());

        // Set list chi tiết các sản phẩm và size trong đơn hàng
        orderDTO.setOrderDetails(orderDetailDTOS);

        response.setCode(HttpStatus.OK.value());
        response.setMessage("Get order info successfully");
        response.setData(orderDTO);
        return response;
    }

    @Override
    public BaseResponse<?> createOrder(OrderDTO orderDTO) {
        BaseResponse<OrderDTO> response = new BaseResponse<>();
        UserDTO userDTO = userService.getCurrentUser(true);
        Optional<UserEntity> user = userRepository.findById(userDTO.getId());

        // Kiểm tra người dùng
        if (user.isEmpty()) {
            response.setCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage("User not exists in system");
            return response;
        }

        // Tạo mã đơn hàng
        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");

        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setCode(formatter.format(cld.getTime()) + userDTO.getId());
        orderEntity.setStatus(Constant.ORDER_STATUS_UNPAID);
        orderEntity.setUserEntity(user.get());
        orderEntity.setTotalAmount(orderDTO.getTotalAmount());
        LocalDateTime now = LocalDateTime.now();
        orderEntity.setCreatedDate(now);
        orderEntity.setCreatedBy(userDTO.getUsername());

        // Lưu đơn hàng
        OrderEntity orderSave = orderRepository.save(orderEntity);

        // Khởi tạo OrderDTO
        OrderDTO orderResponse = new OrderDTO();
        orderResponse.setId(orderSave.getId());
        orderResponse.setCode(orderSave.getCode());
        response.setMessage("Save order successfully");
        response.setCode(HttpStatus.OK.value());
        response.setData(orderResponse);

        return response;
    }



    public BaseResponse<?> updateOrder(OrderDTO orderDTO) {
        BaseResponse<?> response = new BaseResponse<>();
        UserDTO userDTO = userService.getCurrentUser(true);
        Optional<OrderEntity> order = orderRepository.getByCode(userDTO.getId(), orderDTO.getCode());

        if (order.isEmpty()) {
            response.setCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage("Order not exists in system");
            return response;
        }

        OrderEntity orderEntity = order.get();

        // Cập nhật thông tin cơ bản
        if (orderDTO.getPhoneShipping() != null) {
            orderEntity.setPhoneShipping(orderDTO.getPhoneShipping());
        }

        if (orderDTO.getNote() != null) {
            orderEntity.setNote(orderDTO.getNote());
        }
        if (orderDTO.getCheckInDate() != null) {
            orderEntity.setCheckInDate(orderDTO.getCheckInDate());
        }
        if (orderDTO.getStatus() != null) {
            order.get().setStatus(orderDTO.getStatus());
        }
        if (orderDTO.getCheckOutDate() != null) {
            orderEntity.setCheckOutDate(orderDTO.getCheckOutDate());
        }

        // Tính toán tổng tiền mới nếu có ngày check-in và check-out
        if (orderDTO.getCheckInDate() != null && orderDTO.getCheckOutDate() != null) {
            long daysBetween = ChronoUnit.DAYS.between(orderDTO.getCheckInDate(), orderDTO.getCheckOutDate());
            if (daysBetween <= 0) {
                daysBetween = 1;  // Nếu ít hơn 1 ngày, mặc định là 1 ngày
            }

            // Lấy giá trị totalAmount từ orderDTO hoặc orderEntity nếu bị null
            double totalAmount = orderDTO.getTotalAmount() != null ? orderDTO.getTotalAmount() :
                    (orderEntity.getTotalAmount() != null ? orderEntity.getTotalAmount() : 0.0);

            double newTotalAmount = totalAmount * daysBetween;

            // Gán tổng tiền mới cho orderEntity
            orderEntity.setTotalAmount(newTotalAmount);
        }

        orderEntity.setLastModifiedBy(userDTO.getUsername());
        orderRepository.save(orderEntity);

        response.setMessage("Update order successfully. New total amount: " + orderEntity.getTotalAmount());
        response.setCode(HttpStatus.OK.value());
        return response;
    }

    @Override
    public BaseResponse<List<OrderDTO>> getOrderByUser() {
        return null;
    }

    @Override
    public BaseResponse<?> createOrderDetail(OrderDetailDTO orderDetailDTO) {
        BaseResponse<?> response = new BaseResponse<>();

        Optional<OrderEntity> order = orderRepository.findById(orderDetailDTO.getOrderId());
        if (order.isEmpty()) {
            response.setCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage("Order not exits in system");
            return response;
        }

        Optional<RoomEntity> product = roomRepository.findById(orderDetailDTO.getProductId());
        if (product.isEmpty()) {
            response.setCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage("Product not exits in system");
            return response;
        }


        OrderDetailEntity orderDetailEntity = new OrderDetailEntity();
        orderDetailEntity.setOrderEntity(order.get());
        orderDetailEntity.setRoomEntity(product.get());
        orderDetailEntity.setQuantity(orderDetailDTO.getQuantity());
        orderDetailEntity.setTotal(orderDetailDTO.getTotal());
        orderDetailRepository.save(orderDetailEntity);
        response.setMessage("Save order detail successfully");
        response.setCode(HttpStatus.OK.value());
        return response;
    }

    public BaseResponse<?> getInvoicePdf() throws FileNotFoundException {
        File directory = new File(Constant.INVOICE_PDF_PATH);
        if (!directory.exists()) { //Kiểm tra thư mục đã tồn tại chưa
            directory.mkdirs();//Tạo các thư mục theo path cần thiết
        }
        PdfWriter pdfWriter = new PdfWriter(Constant.INVOICE_PDF_PATH + "invoice.pdf");
        PdfDocument pdfDocument = new PdfDocument(pdfWriter);
        pdfDocument.setDefaultPageSize(PageSize.A4);
        Document document = new Document(pdfDocument);
        // Column mã đơn hàng và ngày mua
        float[] codeAndCreatedAtColumnWidth = {Constant.WIDTH_PDF / 2 + 150f, Constant.WIDTH_PDF / 2};
        // Tạo table chứa column
        Table table = new Table(codeAndCreatedAtColumnWidth);
        table.addCell(new Cell().add("0234564575658678").setFontSize(20f).setBorder(Border.NO_BORDER).setBold());
        Table nestedTable = new Table(new float[]{Constant.WIDTH_PDF / 2, Constant.WIDTH_PDF / 2});
        nestedTable.addCell(getHeaderTextCell("Ngay mua"));
        nestedTable.addCell(getHeaderTextCellValue("31/12/2022"));

        table.addCell(new Cell().add(nestedTable).setBorder(Border.NO_BORDER));

        Border gb = new SolidBorder(Color.GRAY, 2f);
        float[] fullWidth = {Constant.WIDTH_PDF};
        Table divider = new Table(fullWidth);
        divider.setBorder(gb);
        document.add(table);

        document.add(divider);

        float[] twoColumnWidth = {Constant.WIDTH_PDF / 2, Constant.WIDTH_PDF / 2};
        Table twoColumnTable = new Table(twoColumnWidth);
        twoColumnTable.addCell(getBillingAndShippingCell("Billing information"));
        twoColumnTable.addCell(getBillingAndShippingCell("Shipping information"));
        document.add(twoColumnTable.setMarginBottom(12f));

        Table twoColumnTableO = new Table(twoColumnWidth);
        twoColumnTableO.addCell(getCellLeft("Company", true));
        twoColumnTableO.addCell(getCellLeft("Name", true));
        twoColumnTableO.addCell(getCellLeft("Coding error", false));
        twoColumnTableO.addCell(getCellLeft("Coding", false));
        document.add(twoColumnTableO);

        Table twoColumnTableT = new Table(twoColumnWidth);
        twoColumnTableT.addCell(getCellLeft("Name", true));
        twoColumnTableT.addCell(getCellLeft("Address", true));
        twoColumnTableT.addCell(getCellLeft("Abcdef", false));
        twoColumnTableT.addCell(getCellLeft("Ho Tung Mau\nHa Noi", false));
        document.add(twoColumnTableT);

        float[] oneColumnWidth = {Constant.WIDTH_PDF/2+150f};

        Table oneColumnTable = new Table(oneColumnWidth);
        oneColumnTable.addCell(getCellLeft("Phone", true));
        oneColumnTable.addCell(getCellLeft("099999999999", false));
        oneColumnTable.addCell(getCellLeft("Email", true));
        oneColumnTable.addCell(getCellLeft("9999@gamil.com", false));
        document.add(oneColumnTable);

        Table dividerO = new Table(fullWidth);

        Border dgb = new DashedBorder(Color.GRAY, 0.5f);
        document.add(dividerO.setBorder(dgb));

        Paragraph productParagraph = new Paragraph("Products");
        document.add(productParagraph.setBold());

        float[] fiveColumnWidth = {Constant.WIDTH_PDF/5+24f, Constant.WIDTH_PDF/5-8f,Constant.WIDTH_PDF/5-8f,Constant.WIDTH_PDF/5-8f,Constant.WIDTH_PDF/5};
        Table fiveColumnTable = new Table(fiveColumnWidth);
        fiveColumnTable.setBackgroundColor(Color.BLACK, 0.7f);
        fiveColumnTable.addCell(new Cell().add("Description").setBold().setFontColor(Color.WHITE).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.LEFT));
        fiveColumnTable.addCell(new Cell().add("Price").setBold().setFontColor(Color.WHITE).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.JUSTIFIED));
        fiveColumnTable.addCell(new Cell().add("Size").setBold().setFontColor(Color.WHITE).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.JUSTIFIED));
        fiveColumnTable.addCell(new Cell().add("Quantity").setBold().setFontColor(Color.WHITE).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.JUSTIFIED));
        fiveColumnTable.addCell(new Cell().add("Total").setBold().setFontColor(Color.WHITE).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.RIGHT));

        document.add(fiveColumnTable);
        RoomDTO roomDTOO = new RoomDTO();
        roomDTOO.setName("Test Product PDF 1");
        RoomDTO roomDTOT = new RoomDTO();
        roomDTOT.setName("Test Product PDF 2");
        RoomDTO roomDTOTH = new RoomDTO();
        roomDTOTH.setName("Test Product PDF 3");
        RoomDTO roomDTOF = new RoomDTO();
        roomDTOF.setName("Test Product PDF 4");
        RoomDTO roomDTOFI = new RoomDTO();
        roomDTOFI.setName("Test Product PDF 5");

        List<RoomDTO> roomDTOS = new ArrayList<>();
        roomDTOS.add(roomDTOO);
        roomDTOS.add(roomDTOT);
        roomDTOS.add(roomDTOTH);
        roomDTOS.add(roomDTOF);
        roomDTOS.add(roomDTOFI);

        Table fiveColumnTableT = new Table(fiveColumnWidth);
        for (RoomDTO product : roomDTOS) {
            fiveColumnTableT.addCell(new Cell().add(product.getName()).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.LEFT));
            fiveColumnTableT.addCell(new Cell().add(product.getName()).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.JUSTIFIED));
            fiveColumnTableT.addCell(new Cell().add(product.getName()).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.JUSTIFIED));
            fiveColumnTableT.addCell(new Cell().add(product.getName()).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.JUSTIFIED));
            fiveColumnTableT.addCell(new Cell().add(product.getName()).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.RIGHT));
        }

        document.add(fiveColumnTableT.setMarginBottom(20f).setBorder(Border.NO_BORDER));

        document.close();


        BaseResponse<?> response = new BaseResponse<>();
        response.setCode(HttpStatus.OK.value());
        response.setMessage("Get invoice pdf successfully");
        return response;
    }

    private static Cell getHeaderTextCell(String textValue) {
        return new Cell().add(textValue).setBold().setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.RIGHT);
    }

    private static Cell getHeaderTextCellValue(String textValue) {
        return new Cell().add(textValue).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.LEFT);
    }

    private static Cell getBillingAndShippingCell(String textValue) {
        return new Cell().add(textValue).setFontSize(12f).setBold().setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.LEFT);
    }

    private static Cell getCellLeft(String textValue, boolean isBold) {
        Cell cell = new Cell().add(textValue).setFontSize(10f).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.LEFT);
        return isBold ? cell.setBold() : cell;
    }
}
