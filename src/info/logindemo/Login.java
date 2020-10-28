package info.logindemo;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

/**
 * Servlet implementation class Login
 */
@WebServlet("/Login")
public class Login extends HttpServlet {
	private static final long serialVersionUID = 1L;

	// データソースの作成
	DataSource ds;

	// 初期化処理
	public void init() throws ServletException {
		try {
			// 初期コンテキストを取得
			InitialContext ic = new InitialContext();
			// ルックアップしてデータソースを取得
			ds = (DataSource) ic.lookup("java:comp/env/jdbc/searchman");
		} catch (Exception e) {

		}
	}

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Login() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// initial settings of DB
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rset = null;

		// setting text code
		request.setCharacterEncoding("Windows-31J");

		// get the id in index.jsp
		String inputId = request.getParameter("ID");

		// get the password in index.jsp
		String inputPassword = request.getParameter("Password");

		System.out.println(inputId);
		System.out.println(inputPassword);

		try {
			// register JDBC Driver
			// Class.forName("com.mysql.jdbc.Driver");
			// // creating Connection
			// conn =
			// DriverManager.getConnection("jdbc:mysql://localhost:3306/budgetbook?serverTimezone=UTC&useSSL=false",
			// "suser", "spass");

			// データソースからConnectionを取得
			conn = ds.getConnection();

			// preparing for creating sql
			StringBuffer sql = new StringBuffer();

			// creating sql from name
			sql.append("select id, password from logintable");
			
			// display sql
			System.out.println(sql);

			// display sql sentence
			pstmt = conn.prepareStatement(new String(sql));

			// execute sql
			pstmt.execute();

			// put the executed result into ResultSet class
			rset = pstmt.executeQuery();
			
//			String sql = "select id, password from logintable";
//			pstmt = conn.prepareStatement(sql);
//			rset = pstmt.executeQuery();

			// test
			while (rset.next()) {
				System.out.println(rset.getString("id") + ", " + rset.getString("password"));
			}

			// transfer the data to the transition page(put it by Attribute)
			request.setAttribute("SqlResult", rset);

			// move on to loginResult.jsp or loginResultFailed.jsp
			if (inputId.equals(rset.getString("id")) && inputPassword.equals(rset.getString("password"))) {
				//request.getRequestDispatcher("/loginResult.jsp").forward(request, response);
				//response.sendRedirect
				request.getRequestDispatcher("loginResult.jsp").forward(request, response);
			} else {
				//request.getRequestDispatcher("/loginResultFailed.jsp").forward(request, response);
				request.getRequestDispatcher("loginResultFailed.jsp").forward(request, response);
			}

			// terminate the used objects
			rset.close();
			pstmt.close();
			conn.close();

		} catch (Exception e) {
			e.printStackTrace();

			doGet(request, response);
		} finally {
			try {
				// just in case, terminate the DB connection with finally statement
				conn.close();
			} catch (Exception e) {
			}
		}

	}
}
