import { Ionicons } from '@expo/vector-icons';
import { useLocalSearchParams, useRouter } from 'expo-router';
import React, { useEffect, useState } from 'react';
import { ScrollView, StatusBar, Text, TouchableOpacity, View } from 'react-native';

const TermsAndPolicy = () => {
    const router = useRouter();
    const params = useLocalSearchParams();
    const [activeTab, setActiveTab] = useState<'terms' | 'policy'>('terms');

    useEffect(() => {
        if (params.tab === 'policy' || params.tab === 'terms') {
            setActiveTab(params.tab);
        }
    }, [params.tab]);

    const SectionTitle = ({ title, marginTop = 'mt-3' }: { title: string, marginTop?: string }) => (
        <Text className={`text-white text-lg font-bold ${marginTop} mb-2`}>{title}</Text>
    );

    const Paragraph = ({ text }: { text: string }) => (
        <Text className="text-gray-300 text-[16px] leading-6 mb-4 text-justify">{text}</Text>
    );

    const TermsContent = () => (
        <View>
            <Paragraph text="Xin vui lòng đọc các điều khoản sau cẩn thận trước khi sử dụng dịch vụ thanh toán trực tuyến. Với việc truy cập vào phần này của website/ứng dụng, bạn cam kết đã đồng ý với các điều khoản sử dụng của chúng tôi. Các điều khoản này có thể thay đổi theo thời gian và bạn sẽ phải tuân theo các điều khoản được hiển thị từ thời điểm bạn đọc được các điều khoản này." />

            <Paragraph text="Sau khi đã xác nhận các thông tin của khách hàng cung cấp về giao dịch không thành công, tùy theo từng loại tài khoản khách hàng sử dụng mà việc hoàn tiền sẽ có thời gian khác nhau:" />

            <View className="pl-2">
                <Paragraph text="1. Thẻ ATM (Nội địa): hoàn tiền trong 1 tuần làm việc" />
                <Paragraph text="2. Thẻ VISA/ MasterCard (Nội địa): hoàn tiền trong 1 tháng làm việc" />
            </View>

            <Paragraph text="NCC luôn luôn mong muốn đem đến những giây phút giải trí tuyệt vời cho khách hàng với chất lượng dịch vụ tốt nhất. Dưới đây sẽ là một số hướng dẫn cho chính sách thanh toán vé trực tuyến." />

            <SectionTitle title="1. ĐỐI TƯỢNG ÁP DỤNG" />
            <Paragraph text={`Chương trình thanh toán online chỉ áp dụng cho các suất chiếu quy định tại Trung tâm Chiếu phim Quốc gia. Việc đăng kí tham gia Thành Viên Trung tâm Chiếu phim Quốc gia là hoàn toàn miễn phí. Nếu bạn có nhu cầu mua vé với số lượng lớn hơn, vui lòng liên hệ 0243.514.8647 hoặc điền thông tin vào form đăng ký "Đặt vé nhóm, tập thể" tại Website/Ứng dụng, NCC chủ động liên hệ lại với khách hàng`} />

            <SectionTitle title="2. CHÍNH SÁCH HOÀN VÉ HAY ĐỔI VÉ" />
            <Paragraph text="Trên website, giá vé được quy định là giá vé niêm yết, trừ trường hợp có thông báo khác. Trung tâm Chiếu phim Quốc gia không chấp nhận hoàn tiền, hủy vé, đổi vé đã thanh toán thành công. Trung tâm Chiếu phim Quốc gia chỉ thực hiện hoàn tiền trong trường hợp khi giao dịch, tài khoản của bạn đã bị trừ tiền nhưng hệ thống của chúng tôi không ghi nhận việc đặt vé của bạn, và bạn không nhận được xác nhận đặt vé thành công; hoặc buổi chiếu bị hủy. Khi đó, bạn vui lòng liên hệ với Trung tâm Chiếu phim Quốc gia : 024 35141791 hoặc Zalo OA Trung tâm Chiếu phim Quốc gia trong 48 giờ làm việc." />
            <Paragraph text="Trước khi thanh toán vé trực tuyến, chúng tôi khuyên bạn nên xác nhận lại Tên phim, Giờ chiếu và Rạp chiếu của bộ phim bạn muốn xem." />

            <SectionTitle title="3. THƯ VÀ XÁC NHẬN TIN NHẮN ĐẶT VÉ" />
            <Paragraph text="Sau khi hoàn thành việc thanh toán vé trực tuyến, bạn sẽ nhận được thư xác nhận thông tin chi tiết vé đã thanh toán thông qua địa chỉ thư điện tử (email) mà bạn đã cung cấp. Ngoài ra, khi đặt vé tại Website bạn sẽ nhận được một tin nhắn miễn phí, xác nhận mã số đặt vé và các thông tin vé đã đặt. Lưu ý email và tin nhắn này chỉ có tính chất dự phòng. Do đó, chúng tôi đề nghị bạn khi tiến hành các bước thanh toán, cần đọc kĩ các thông tin trên màn hình về rạp chiếu phim, tên phim, suất chiếu, và chỗ ngồi trước khi hoàn tất việc xác nhận tất cả các thông tin về vé." />
            <Paragraph text="Email xác nhận thông tin đặt vé có thể đi vào hộp thư rác (spam mail) của bạn, vì vậy hãy kiểm tra chúng trước khi liên lạc với chúng tôi. Nếu bạn có thắc mắc hoặc gặp vấn đề với việc đặt vé của bạn, bạn có thể liên hệ với chúng tôi theo số điện thoại: 02435141791 để được hỗ trợ." />

            <SectionTitle title="4. NHẬN VÉ" />
            <Paragraph text="Sau khi đặt vé thành công, tại mục “Vé đã mua” bạn nhận được mã QR thì sử dụng mã này lên thẳng phòng chiếu, không cần đổi sang vé giấy; trường hợp bạn nhận được sms hoặc gmail chứa mã code thì ghi nhớ hoặc chụp lại mã để đổi sang vé giấy tại máy bán vé tự động đặt tại tầng 1 Trung tâm Chiếu phim Quốc gia Bên cạnh đó, bạn cần mang theo giấy tờ tùy thân có ảnh của bạn như CCCD, thẻ học sinh/sinh viên hoặc passport để đối chiếu khi cần thiết. Bằng việc thanh toán qua website này, bạn chấp nhận vị trí ghế ngồi mà bạn đã đặt. Bạn đồng ý rằng, trong những trường hợp có sự thay đổi về chương trình phim hoặc bất khả kháng, chúng tôi có quyền hoàn trả lại bất kỳ vé nào từ việc mua bán qua trang website/ứng dụng này hoặc thực hiện việc chuyển vé cho bạn qua suất chiếu khác theo yêu cầu của bạn." />

            <SectionTitle title="5. PHÍ ĐẶT VÉ" />
            <Paragraph text="Những vé thanh toán trên website/ứng dụng Trung tâm Chiếu phim Quốc gia sẽ phải chấp nhận một phụ phí không hoàn lại gọi là Phí Đặt Vé, ngoại trừ các trường hợp đặc biệt khi Trung tâm Chiếu phim Quốc gia không thể cung cấp cho bạn vé đã đặt" />
            <Paragraph text="Hiện tại mức phí này là 0 VND cho tất cả các giao dịch thanh toán vé online. Tuy nhiên, mức phí này có thể thay đổi bất cứ khi nào. Chúng tôi sẽ thông báo đến bạn khi có thay đổi." />

            <SectionTitle title="6. THUẾ GIÁ TRỊ GIA TĂNG" />
            <Paragraph text="Thuế giá trị gia tăng (GTGT) được áp dụng cho tất cả các mặt hàng và dịch vụ trên trang mạng này. Các đơn giá trên trang mạng này đã bao gồm GTGT." />

            <SectionTitle title="7. CHỨC NĂNG CHỐNG GIAN LẬN" />
            <Paragraph text="- Trung tâm Chiếu phim Quốc gia (hoặc bên thứ ba - nhà cung cấp cổng thanh toán điện tử, hoặc/và các bên ký kết khác) sẽ sử dụng các công nghệ đặc biệt để nhận biết các hoạt động giả mạo trên trang mạng, ví dụ: sử dụng thẻ tín dụng giả. Sự chấp nhận hợp tác của bạn cùng với nỗ lực của Trung tâm Chiếu phim Quốc gia là rất cần thiết. Bạn chấp nhận rằng Trung tâm Chiếu phim Quốc gia có thể chấm dứt quyền truy cập và sử dụng trang mạng của Trung tâm Chiếu phim Quốc gia nếu bạn hoặc người khác hành động nhân danh bạn nằm trong diện nghi vấn có gian lận hoặc vi phạm các quy định của Trung tâm." />
            <Paragraph text="- Nếu bạn phát hiện thẻ của mình bị sử dụng giả mạo để mua hàng trên trang mạng này, bạn hãy lập tức liên hệ với ngân hàng phát hành thẻ của bạn theo quy trình." />

            <SectionTitle title="11. CÁNH BÁO AN NINH" />
            <Paragraph text="Trung tâm Chiếu phim Quốc gia sẽ hết sức cố gắng sử dụng mọi biện pháp và theo mọi cách thức có thể đảm bảo an toàn cho tất cả các thông tin cá nhân của bạn, và chúng tôi cũng sẽ thường xuyên cập nhật những thông tin chính xác nhất. Trang mạng này có những công nghệ an ninh đảm bảo việc bảo vệ các thông tin không bị thất lạc, lạm dụng hoặc thay đổi. Tất cả các giao dịch và thông tin về thẻ được sử dụng đều được mã hóa qua công nghệ Secure Sockets Layer (SSL). Đó là phần mềm tốt nhất về đảm bảo an toàn cho các giao dịch kinh tế ngày nay. Mặc dù vậy, không phải tất cả các dữ liệu truyền qua Internet đều có thể đảm bảo 100%, vì thế chúng tôi không thể đưa ra một sự đảm bảo tuyệt đối rằng mọi thông tin bạn cung cấp đều được bảo vệ tất cả mọi lúc, mọi nơi." />

            <SectionTitle title="LIÊN HỆ" />
            <Paragraph text="Các bạn có thể liên hệ với chúng tôi theo số điện thoại: 0865.205.608 để được hỗ trợ thêm." />
        </View>
    );

    const PolicyContent = () => (
        <View>
            <SectionTitle title="1. Mục đích và phạm vi thu thập thông tin" marginTop="mt-0" />
            <Paragraph text="1.1. Việc thu thập thông tin cá nhân được thực hiện trên cơ sở khách hàng tự khai báo để đăng ký thành viên Trung tâm Chiếu phim Quốc gia (NCC) tại website https://chieuphimquocgia.com.vn/, tùy từng thời điểm, thông tin thu thập sẽ bao gồm nhưng không giới hạn ở:" />
            <View className="pl-2">
                <Paragraph text="- Thông tin cá nhân như: họ tên, giới tính, độ tuổi, số CMND" />
                <Paragraph text="- Thông tin liên lạc như: địa chỉ, số điện thoại di động, email/fax" />
                <Paragraph text="- Các thông tin khác phục vụ cho chương trình khách hàng thân thiết (nếu có)" />
            </View>
            <Paragraph text="1.2. Mục đích thu thập thông tin khách hàng bao gồm:" />
            <View className="pl-2">
                <Paragraph text="- Cung cấp các dịch vụ, sản phẩm theo nhu cầu của khách hàng" />
                <Paragraph text="- Liên hệ xác nhận khi khách hàng đăng ký sử dụng dịch vụ, xác lập giao dịch trên website https://chieuphimquocgia.com.vn/" />
                <Paragraph text="- Thực hiện việc quản lý website https://chieuphimquocgia.com.vn/, gửi thông tin cập nhật về website, các chương trình khuyến mại, ưu đãi/tri ân tới khách hàng" />
                <Paragraph text="- Bảo đảm quyền lợi của khách hàng khi phát hiện các hành động giả mạo, phá hoại tài khoản, lừa đảo khách hàng" />
            </View>
            <Paragraph text="1.3. Để tránh nghi ngờ, trong quá trình giao dịch thanh toán tại website https://chieuphimquocgia.com.vn/, Trung tâm Chiếu phim Quốc gia chỉ lưu giữ thông tin chi tiết về đơn hàng đã thanh toán của khách hàng, các thông tin về tài khoản ngân hàng của khách hàng sẽ không được lưu giữ." />

            <SectionTitle title="2. Phạm vi sử dụng thông tin" />
            <Paragraph text="2.1. Trung tâm Chiếu phim Quốc gia chỉ sử dụng thông tin cá nhân của khách hàng cho các mục đích quy định tại Mục 1 hoặc mục đích khác (nếu có) với điều kiện đã thông báo và được sự đồng ý của khách hàng." />
            <Paragraph text="2.2. Trung tâm Chiếu phim Quốc gia sẽ không sử dụng thông tin cá nhân của khách hàng để gửi quảng cáo, giới thiệu dịch vụ và các thông tin có tính thương mại khác khi chưa được khách hàng chấp thuận." />
            <Paragraph text="2.3. Khách hàng hiểu và đồng ý rằng Trung tâm Chiếu phim Quốc gia có nghĩa vụ phải cung cấp thông tin khách hàng theo yêu cầu/quyết định của Cơ quan nhà nước có thẩm quyền và/hoặc quy định pháp luật. Trung tâm Chiếu phim Quốc gia sẽ được miễn trừ mọi trách nhiệm liên quan đến bảo mật thông tin trong trường hợp này." />

            <SectionTitle title="3. Thời gian lưu trữ thông tin" />
            <Paragraph text="Dữ liệu cá nhân cơ bản của khách hàng đăng ký thành viên Trung tâm Chiếu phim Quốc gia sẽ được lưu trữ cho đến khi có yêu cầu hủy bỏ hoặc tự thành viên đăng nhập và thực hiện đóng tài khoản. Đối với các tài khoản đã đóng chúng tôi vẫn lưu trữ thông tin cá nhân và truy cập của khách hàng để phục vụ cho mục đích phòng chống gian lận, điều tra, giải đáp thắc mắc ... Các thông tin này sẽ được lưu trữ trong hệ thống máy chủ tối đa mười hai (12) tháng. Hết thời hạn này, Trung tâm Chiếu phim Quốc gia sẽ tiến hành xóa vĩnh viễn thông tin cá nhân của khách hàng." />

            <SectionTitle title="4. Cách thức chỉnh sửa dữ liệu cá nhân" />
            <Paragraph text="Để chỉnh sửa dữ liệu cá nhân của mình trên hệ thống thương mại điện tử của Trung tâm Chiếu phim Quốc gia, khách hàng có thể tự đăng nhập và chỉnh sửa thông tin, dữ liệu cá nhân, ngoại trừ các thông tin về Họ tên, Giới tính, Ngày sinh và Chứng minh nhân dân." />

            <SectionTitle title="5. Trung tâm Chiếu phim Quốc gia cam kết" />
            <Paragraph text="5.1. Mọi thông tin cá nhân của khách hàng thu thập được từ website https://chieuphimquocgia.com.vn/ sẽ được lưu giữ an toàn; chỉ có khách hàng mới có thể truy cập vào tài khoản cá nhân của mình bằng tên đăng nhập và mật khẩu do khách hàng chọn." />
            <Paragraph text="5.2. Trung tâm Chiếu phim Quốc gia cam kết bảo mật thông tin, không chia sẻ, tiết lộ, chuyển giao thông tin cá nhân của khách hàng, thông tin giao dịch trực tuyến trên website https://chieuphimquocgia.com.vn/ cho bất kỳ bên thứ ba nào khi chưa được sự đồng ý của khách hàng, trừ trường hợp phải thực hiện theo yêu cầu của các cơ quan Nhà nước có thẩm quyền, hoặc theo quy định của pháp luật hoặc việc cung cấp thông tin đó là cần thiết để Trung tâm Chiếu phim Quốc gia cung cấp dịch vụ/ tiện ích cho khách hàng." />
            <Paragraph text="5.3. Trung tâm Chiếu phim Quốc gia, bằng nỗ lực tốt nhất của mình, sẽ áp dụng các giải pháp công nghệ để ngăn chặn các hành vi đánh cắp hoặc tiếp cận thông tin trái phép; sử dụng, thay đổi hoặc phá hủy thông tin trái phép. Tuy nhiên, Trung tâm Chiếu phim Quốc gia không thể cam kết sẽ ngăn chặn được tất cả các hành vi xâm phạm, sử dụng thông tin cá nhân trái phép nằm ngoài khả năng kiểm soát của Trung tâm Chiếu phim Quốc gia. Trung tâm Chiếu phim Quốc gia sẽ không chịu trách nhiệm dưới bất kỳ hình thức nào đối với bất kỳ khiếu nại, tranh chấp hoặc thiệt hại nào phát sinh từ hoặc liên quan đến việc truy cập, xâm nhập, sử dụng thông tin trái phép như vậy." />
            <Paragraph text="5.4. Trường hợp máy chủ lưu trữ thông tin bị hacker tấn công dẫn đến mất mát dữ liệu cá nhân, gây ảnh hưởng xấu đến khách hàng, Trung tâm Chiếu phim Quốc gia sẽ ngay lập tức thông báo cho khách hàng và trình vụ việc cho cơ quan chức năng điều tra xử lý." />
            <Paragraph text="5.5. Đối với các giao dịch trực tuyến được thực hiện thông qua website https://chieuphimquocgia.com.vn/, Trung tâm Chiếu phim Quốc gia không lưu trữ thông tin thẻ thanh toán của khách hàng. Thông tin tài khoản, thẻ thanh toán của khách hàng sẽ được các đối tác cổng thanh toán của Trung tâm Chiếu phim Quốc gia bảo vệ theo tiêu chuẩn quốc tế." />
            <Paragraph text="5.6. Khách hàng có nghĩa vụ bảo mật tên đăng ký, mật khẩu và hộp thư điện tử của mình. Trung tâm Chiếu phim Quốc gia sẽ không chịu trách nhiệm dưới bất kỳ hình thức nào đối với các thiệt hại, tổn thất (nếu có) do khách hàng không tuân thủ quy định bảo mật này." />
            <Paragraph text="5.7. Khách hàng tuyệt đối không được có các hành vi sử dụng công cụ, chương trình để can thiệp trái phép vào hệ thống hay làm thay dổi dữ liệu của Trung tâm Chiếu phim Quốc gia. Trong trường hợp Trung tâm Chiếu phim Quốc gia phát hiện khách hàng có hành vi cố tình giả mạo, gian lận, phát tán thông tin cá nhân trái phép … Trung tâm Chiếu phim Quốc gia có quyền chuyển thông tin cá nhân của khách hàng cho các cơ quan có thẩm quyền để xử lý theo quy định của pháp luật." />
        </View>
    );

    return (
        <View className="flex-1 bg-[#272b50]">
            <StatusBar barStyle="light-content" />

            <View className="flex-row items-center justify-between px-[15px] pt-[50px] z-10 bg-[#272b50]">
                <TouchableOpacity onPress={() => router.back()} className="p-[5px]">
                    <Ionicons name="arrow-back" size={28} color="white" />
                </TouchableOpacity>
                <Text className="text-white text-[18px] font-medium">Điều khoản & Chính sách</Text>
                <View className="w-[28px]" />
            </View>

            {/* Tabs */}
            <View className="flex-row border-b border-gray-600 bg-[#272b50] z-10 px-5">
                <TouchableOpacity
                    onPress={() => setActiveTab('terms')}
                    className={`flex-1 py-4 items-center justify-center border-b-2 ${activeTab === 'terms' ? 'border-[#1e90ff]' : 'border-transparent'}`}
                >
                    <Text className={`text-[16px] font-bold ${activeTab === 'terms' ? 'text-white' : 'text-gray-400'}`}>Điều khoản</Text>
                </TouchableOpacity>
                <TouchableOpacity
                    onPress={() => setActiveTab('policy')}
                    className={`flex-1 py-4 items-center justify-center border-b-2 ${activeTab === 'policy' ? 'border-[#1e90ff]' : 'border-transparent'}`}
                >
                    <Text className={`text-[16px] font-bold ${activeTab === 'policy' ? 'text-white' : 'text-gray-400'}`}>Chính sách</Text>
                </TouchableOpacity>
            </View>

            <ScrollView className="flex-1 px-5 pt-4 pb-10" showsVerticalScrollIndicator={false}>
                {activeTab === 'terms' ? <TermsContent /> : <PolicyContent />}
                <View className="h-10" />
            </ScrollView>
        </View>
    );
};

export default TermsAndPolicy;
