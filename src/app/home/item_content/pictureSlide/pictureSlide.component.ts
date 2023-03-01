import {Component, OnInit} from "@angular/core";
import {ModalDialogParams} from "@nativescript/angular";
import {Book} from "../../assets/lists/book";

;

@Component({
  selector:'ns-content-item',
  templateUrl:'./itemContent.component.html',
  styleUrls:['./itemContent.component.scss']
})
export class ItemContentComponent implements OnInit{
  book:Book;
  constructor(private modalParams:ModalDialogParams) {
  }
  ngOnInit(){
    const parseParams = (this.modalParams.context as {data:Book})
    this.book = parseParams.data
  }
}
