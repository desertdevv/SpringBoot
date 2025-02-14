package kh.springboot.member.model.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString

public class TodoList {
	private int todoNum;
	private String todo;
	private String writer;
	private String status;
	private String important;
}
