import {Component, OnInit, ViewContainerRef} from '@angular/core';
import {ItemEventData, Page} from "@nativescript/core";
import {Book} from "../../assets/lists/book";
import {Fiction} from "../../assets/lists/gener";
import {Condition} from "../../assets/lists/condition";
import {ModalDialogService} from "@nativescript/angular";
import {UIService} from "../../shared/ui/ui.service";
import {ItemContentComponent} from "../item_content/itemContent.component";

@Component({
  selector: 'app-search',
  templateUrl: './search.component.html',
  styleUrls: ['./search.component.scss']
})
export class SearchComponent implements OnInit{
  list_books:Book[]=[{
    name:'ahoj',
    author:'autor',
    gener:Fiction.FANTASY,
    condition:Condition.LIKE_NEW,
    price:'price'
  },
    {
      name:'ahoj',
      author:'autor',
      gener:Fiction.FANTASY,
      condition:Condition.LIKE_NEW,
      price:'price'
    },
    {
      name:'ahoj',
      author:'autor',
      gener:Fiction.FANTASY,
      condition:Condition.LIKE_NEW,
      price:'price'
    },
    {
      name:'ahoj',
      author:'autor',
      gener:Fiction.FANTASY,
      condition:Condition.LIKE_NEW,
      price:'price'
    },
    {
      name:'ahoj',
      author:'autor',
      gener:Fiction.FANTASY,
      condition:Condition.LIKE_NEW,
      price:'price'
    },
    {
      name:'ahoj',
      author:'autor',
      gener:Fiction.FANTASY,
      condition:Condition.LIKE_NEW,
      price:'price'
    },
    {
      name:'ahoj',
      author:'autor',
      gener:Fiction.FANTASY,
      condition:Condition.LIKE_NEW,
      price:'price'
    },
    {
      name:'ahoj',
      author:'autor',
      gener:Fiction.FANTASY,
      condition:Condition.LIKE_NEW,
      price:'price'
    },{
      name:'ahoj',
      author:'autor',
      gener:Fiction.FANTASY,
      condition:Condition.LIKE_NEW,
      price:'price'
    },{
      name:'ahoj',
      author:'autor',
      gener:Fiction.FANTASY,
      condition:Condition.LIKE_NEW,
      price:'price'
    },
    {
      name:'ahoj',
      author:'autor',
      gener:Fiction.FANTASY,
      condition:Condition.LIKE_NEW,
      price:'price'
    },
    {
      name:'ahoj',
      author:'autor',
      gener:Fiction.FANTASY,
      condition:Condition.LIKE_NEW,
      price:'price'
    },
    {
      name:'ahoj',
      author:'autor',
      gener:Fiction.FANTASY,
      condition:Condition.LIKE_NEW,
      price:'price'
    }
  ]

  constructor(
    private page:Page,
    private modelDialog:ModalDialogService,
    private uiService:UIService,
    private vcRef:ViewContainerRef
  ) {}

  ngOnInit(){
    this.page.actionBarHidden=true
  }

  onItemTap(data:ItemEventData){
    this.modelDialog.showModal(ItemContentComponent,{fullscreen:true,
      viewContainerRef:this.uiService.getRootVCRef()
        ?this.uiService.getRootVCRef()
        :this.vcRef,
      context:{
        data:this.list_books[data.index]
      }
    })
  }
}
