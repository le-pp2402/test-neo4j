package services;

import models.User;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Generator {

	final static String USERS_CSV = "users.csv";
	final static String FRIENDSHIPS_CSV = "friendships.csv";
	final static Path temDir = Paths.get(System.getProperty("java.io.tmpdir"));

    public static List<String> lstLastNames = Arrays.asList("Nguyễn", "Ngô", "Lê", "Trần", "Phan", "Phạm", "Đặng", "Đoàn", "Trịnh", "Đinh", "Đỗ", "Vi", "Dương", "Thái", "Doãn", "Phùng", "Mai", "Mạc", "Vũ", "Trương");
	public static List<String> lstNames = Arrays.asList("Thị", "Văn", "Phương", "Hoài", "Hồng", "Minh", "Mẫn", "Mộc", "Thành", "Phú", "Phúc", "Lộc", "Thu", "Vĩnh", "Trung", "Thành", "An", "Ân", "Ấn", "Ánh", "Bình", "Bi", "Bính", "Cư", "Cảnh", "Cương", "Cường", "Danh", "Du", "Dư", "Dũng", "Dụng", "Diên", "Diễn", "Diễm", "Dự", "Đinh", "Đình", "Định", "Độ", "Đô", "Đông", "Đồng", "Đệ", "Đề", "Đủ",	"Hà", "Hậu", "Hoa", "Hồng", "Hương", "Hùng", "Hữu", "Huy", "Hường", "Hy", "Hạnh", "Hành", "Kiên", "Khôi", "Kha", "Khương", "Khâm", "Khiết", "Khiêm", "Long", "Loan", "Lương", "Lành", "Linh", "Lượng", "Lâm", "Luỹ", "Lựu", "Lưu", "Lĩnh", "Mừng", "Mận", "Miên", "Nam", "Ninh",  "Oanh", "Phương", "Phong", "Phòng", "Phấn", "Pha", "Phin", "Phường", "Phú", "Phu", "Phiên", "Phiến", "Phúc", "Quang", "Quảng", "Quốc", "Quỳnh", "Quý",	"Sa", "Sơn", "Sương", "Sung", "Sùng", "Si", "Tâm", "Tam", "Tăng", "Tấm", "Tình", "Tính", "Tường", "Tương", "Tịnh", "Tú", "Tùng", "Tiên", "Tiến", "Tưởng", "Trường", "Trưởng", "Thu", "Thương", "Thắng", "Thành", "Thanh", "Thảo", "Thao", "Thịnh", "Uy", "Uỳ", "Vân", "Vảng", "Vũ", "Vương", "Vinh", "Vĩnh", "Vĩnh", "Vượng", "Xuân", "Xa", "Xinh", "Xuyến", "Yên", "Yến");

    public static void genDatasets(int nUser, long nFriendships) throws FileNotFoundException {

        lstNames = new ArrayList<>(new HashSet<>(lstNames));
		lstNames.addAll(lstLastNames);
		lstNames = new ArrayList<>(new HashSet<>(lstNames));

        Random rnd = new Random((new java.util.Date()).getTime());

		FileOutputStream oUser = new FileOutputStream(String.valueOf(temDir.resolve(USERS_CSV)));
		FileOutputStream oFriendship = new FileOutputStream(String.valueOf(temDir.resolve(FRIENDSHIPS_CSV)));

		try (var osw = new OutputStreamWriter(oUser, StandardCharsets.UTF_8)) {
			osw.write("id,name\n");
			for (int i = 0; i < nUser; i++) {
                String lastName = lstLastNames.get(rnd.nextInt(lstLastNames.size()));
				String midName = lstNames.get(rnd.nextInt(lstNames.size()));
				String firstName = lstNames.get(rnd.nextInt(lstNames.size()));
				if (rnd.nextInt(10) % 2 == 0)
					firstName = firstName + " " + lstNames.get(rnd.nextInt(lstNames.size()));
				String fullName = lastName + " " + midName + " " + firstName;

				osw.write((i + 1) + "," + fullName);

				if (i < nUser - 1)
					osw.write("\n");
			}
		} catch (Exception exc) {
			System.out.println(exc.getMessage());
			return;
		}

        System.out.printf("Reset (if need) %d -> %d\n", nFriendships, (long) nUser * (nUser - 1));
		nFriendships = Math.min((long) nUser * (nUser - 1), nFriendships);

		try (var osw = new OutputStreamWriter(oFriendship, StandardCharsets.UTF_8)) {

			osw.write("id,user_1,user_2\n");

			HashSet<String> set = new HashSet<>();
			for (long i = 0; i < nFriendships; i++) {
				while (true) {

					int idx1 = rnd.nextInt(nUser) + 1;
					int idx2 = rnd.nextInt(nUser) + 1;
					String rela = String.format("%d -> %d", idx1, idx2);

					if (!set.contains(rela)) {
						osw.write(i+","+idx1 + "," + idx2);
						if (i < nFriendships - 1)
							osw.write("\n");
						set.add(rela);
						break;
					}

				}
			}
		} catch (Exception exc) {
			System.out.println(exc.getMessage());
			return;
		}
		System.out.println("DONE");
    }
}
